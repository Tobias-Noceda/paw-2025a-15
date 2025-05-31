package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.exceptions.AppointmentAlreadyTakenException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

@Service
public class AppointmentServiceImpl implements AppointmentService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private EmailService es;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private AuthDoctorService ads;

    @Transactional
    @Override
    public AppointmentNew addAppointment(long shiftId, long patientId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Patient patient = pds.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        DoctorSingleShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with id: " + shiftId + " not found"));
        Doctor doctor = dds.getDoctorById(shift.getDoctor().getId()).orElseThrow(() -> new NotFoundException("Doctor with id: " + shift.getDoctor().getId() + " does not exist!"));
        
        if(date==null || date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && shift.getStartTime().isBefore(LocalTime.now()))) throw new IllegalArgumentException("Shift must be in a valid datetime");
        
        if(date.getDayOfWeek().ordinal() != shift.getWeekday().ordinal()) throw new IllegalArgumentException("Shift must be on the same day of the week as the appointment date");
        
        getAppointmentByShiftIdDateAndTime(shiftId, date, startTime, endTime).ifPresent(a -> {throw new AppointmentAlreadyTakenException("Shift already taken");});

        AppointmentNew appointment = appointmentDao.addAppointment(shift, (User) patient, date, startTime, endTime);
        if(appointment == null){
            LOGGER.error("Failed to create appointment for patientId: {}, shiftId: {} and date: {} at {}", patientId, shiftId, date, LocalDateTime.now());
            throw new RuntimeException("Failed to create appointment for patientId: " + patientId +", shiftId: " + shiftId +" and date: " + date);
        }
        LOGGER.info("Successfully created appointment for patientId: {}, shiftId: {} and date: {}", patientId, shiftId, date);
        if(patient.getId() != doctor.getId()) {
            es.sendDoctorTakenShiftEmail(patient, doctor, appointment, shift);
            es.sendPatientTakenShiftEmail(patient, doctor, appointment, shift);
            
            if(!ads.hasAuthDoctor(patientId, doctor.getId())) {
                ads.toggleAuthDoctor(patientId, doctor.getId());//grant doctors access to the patient profile
            }
        }
        return appointment;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AppointmentNew> getAppointmentByShiftIdDateAndTime(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        DoctorSingleShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with id: " + shiftId + " does not exist!"));
        return appointmentDao.getAppointmentByShiftDateAndTime(shift, date, startTime, endTime);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentNew> getFutureAppointmentDataByPatientId(long patientId) {
        Patient patient = pds.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return appointmentDao.getFutureAppointmentDataByPatient(patient);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentNew> getOldAppointmentDataByPatientId(long patientId) {
        Patient patient = pds.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return appointmentDao.getOldAppointmentDataByPatient(patient);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentNew> getFutureAppointmentDataByDoctorId(long doctorId) {
        Doctor doctor = dds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return appointmentDao.getFutureAppointmentDataByDoctor(doctor);
    }

    @Transactional
    @Override
    public void cancelAppointment(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime, long cancellerId) {
        AppointmentNew appointment = getAppointmentByShiftIdDateAndTime(shiftId, date, startTime, endTime).orElseThrow(() -> new NotFoundException("Appointment with shiftId: " + shiftId + " and date: " + date + " does not exist!"));
        Patient patient = pds.getPatientById(appointment.getPatient().getId()).orElseThrow(() -> new NotFoundException("Patient with id: " + appointment.getPatient().getId() + " does not exist!"));
        DoctorSingleShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with shiftId: " + shiftId + " does not exist!"));
        Doctor doctor = dds.getDoctorById(shift.getDoctor().getId()).orElseThrow(() -> new NotFoundException("Doctor with id: " + shift.getDoctor().getId() + " does not exist!"));

        if (cancellerId == patient.getId()) {
            if(appointmentDao.cancelAppointment(shift, date, startTime, endTime)){
                LOGGER.info("Patient with id: {} has cancelled their appointment with doctor with id: {} on the shiftId:{} and date {}", patient.getId(), doctor.getId(), shiftId, date);
                es.sendPatientCancellationConfirmationEmail(patient, doctor, appointment, shift);
                es.sendDoctorCancelledAppointmentEmail(patient, doctor, appointment, shift);
            }
        } else if (cancellerId == doctor.getId()) {
            if(appointmentDao.cancelAppointment(shift, date, startTime, endTime)){
                LOGGER.info("Doctor with id: {} has cancelled their appointment with patient with id: {} on the shiftId:{} and date {}", doctor.getId(), patient.getId(), shiftId, date);
                es.sendDoctorCancellationConfirmationEmail(patient, doctor, appointment, shift);
                es.sendPatientCancelledAppointmentEmail(patient, doctor, appointment, shift);
            }
        } else throw new UnauthorizedException("User not authorized to cancel this appointment");
    }

    @Transactional
    @Override
    public void removeAppointment(long shiftId, LocalDate date, long doctorId, LocalTime startTime, LocalTime endTime) {
        DoctorSingleShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with shiftId: " + shiftId + " does not exist!"));
        if(shift.getDoctor().getId() != doctorId) throw new UnauthorizedException("User not authorized to remove this appointment");//TODO:check changed for hibernate

        addAppointment(shiftId, doctorId, date, startTime, endTime);
        LOGGER.info("Doctor with id: {} has removed an appointment from their free appointments at shiftId: {} and date: {}", doctorId, shiftId, date);
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * *", zone = "America/Argentina/Buenos_Aires")
    public void clearRemovedAppointmentBeforeDate() {
        LocalDate date = LocalDate.now().minusDays(1);
        appointmentDao.clearRemovedAppointmentBeforeDate(date);
        LOGGER.info("Removed appointments before " + date + " cleared. At " + LocalDateTime.now().toLocalTime());
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 8 * * ?", zone = "America/Argentina/Buenos_Aires")
    public void rememberTomorrowAppointments() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<AppointmentNew> appointments = appointmentDao.getAppointmentsForDate(date);
        for (AppointmentNew appointment : appointments) {
            es.sendPatientAppointmentReminderEmail(appointment);
            es.sendDoctorAppointmentReminderEmail(appointment);
            LOGGER.info("Sending appointment reminder email to patient {} and doctor {} for appointment on {}", appointment.getPatient().getId(), appointment.getShift().getDoctor().getId(), date);
        }
        LOGGER.info("Tomorrow appointments reminder sent. At " + LocalDateTime.now().toLocalTime());
    }
            
}