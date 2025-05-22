package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
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
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.AppointmentAlreadyTakenException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

@Service
public class AppointmentServiceImpl implements AppointmentService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private EmailService es;

    @Autowired
    private UserService us;

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private AuthDoctorService ads;

    @Transactional
    @Override
    public Appointment addAppointment(long shiftId, long patientId, LocalDate date) {
        User patient = us.getUserById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with id: " + shiftId + " not found"));
        User doctor = us.getUserById(shift.getDoctor().getId()).orElseThrow(() -> new NotFoundException("Doctor with id: " + shift.getDoctor().getId() + " does not exist!"));//TODO:check changed for hibernate
        if(date==null || date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && shift.getStartTime().isBefore(LocalTime.now()))) throw new IllegalArgumentException("Shift must be in a valid datetime");
        
        if(date.getDayOfWeek().ordinal() != shift.getWeekday().ordinal()) throw new IllegalArgumentException("Shift must be on the same day of the week as the appointment date");
        
        getAppointmentsByShiftIdAndDate(shiftId, date).ifPresent(a -> {throw new AppointmentAlreadyTakenException("Shift already taken");});

        Appointment appointment = appointmentDao.addAppointment(shift, patient, date);
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
    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date) {
        return appointmentDao.getAppointmentsByShiftIdAndDate(shiftId, date);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentData> getFutureAppointmentDataByPatientId(long patientId) {
        return appointmentDao.getFutureAppointmentDataByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentData> getOldAppointmentDataByPatientId(long patientId) {
        return appointmentDao.getOldAppointmentDataByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentData> getFutureAppointmentDataByDoctorId(long doctorId) {
        return appointmentDao.getFutureAppointmentDataByDoctorId(doctorId);
    }

    @Transactional
    @Override
    public void cancelAppointment(long shiftId, LocalDate date, long cancelId) {
        Appointment appointment = getAppointmentsByShiftIdAndDate(shiftId, date).orElseThrow(() -> new NotFoundException("Appointment with shiftId: " + shiftId + " and date: " + date + " does not exist!"));
        User patient = us.getUserById(appointment.getPatient().getId()).orElseThrow(() -> new NotFoundException("Patient with id: " + appointment.getPatient().getId() + " does not exist!"));//TODO:check changed for hibernate
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with shiftId: " + shiftId + " does not exist!"));
        User doctor = us.getUserById(shift.getDoctor().getId()).orElseThrow(() -> new NotFoundException("Doctor with id: " + shift.getDoctor().getId() + " does not exist!"));//TODO:check changed for hibernate

        if(cancelId==patient.getId()){
            if(appointmentDao.removeAppointment(shiftId, date)){
                LOGGER.info("Patient with id: {} has cancelled their appointment with doctor with id: {} on the shiftId:{} and date {}", patient.getId(), doctor.getId(), shiftId, date);
                es.sendPatientCancellationConfirmationEmail(patient, doctor, appointment, shift);
                es.sendDoctorCancelledAppointmentEmail(patient, doctor, appointment, shift);
            }
        } else if(cancelId==doctor.getId()) {
            if(appointmentDao.removeAppointment(shiftId, date)){
                LOGGER.info("Doctor with id: {} has cancelled their appointment with patient with id: {} on the shiftId:{} and date {}", doctor.getId(), patient.getId(), shiftId, date);
                es.sendDoctorCancellationConfirmationEmail(patient, doctor, appointment, shift);
                es.sendPatientCancelledAppointmentEmail(patient, doctor, appointment, shift);
            }
        } else throw new UnauthorizedException("User not authorized to cancel this appointment");
    }

    @Transactional
    @Override
    public void removeAppointment(long shiftId, LocalDate date, long doctorId) {
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with shiftId: " + shiftId + " does not exist!"));
        if(shift.getDoctor().getId() != doctorId) throw new UnauthorizedException("User not authorized to remove this appointment");//TODO:check changed for hibernate

        addAppointment(shiftId, doctorId, date);
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
        List<Appointment> appointments = appointmentDao.getAppointmentsForDate(date);
        for (Appointment appointment : appointments) {
            us.getUserById(appointment.getPatient().getId()).ifPresent(patient -> {//TODO:check changed for hibernate
                DoctorShift shift = dss.getShiftById(appointment.getShift().getId()).orElse(null);//TODO:check changed for hibernate
                if (shift != null) {
                    us.getUserById(shift.getDoctor().getId()).ifPresent(doctor -> {//TODO:check changed for hibernate
                        Locale patientLocale = patient.getLocale().toLocale();
                        Locale doctorLocale = doctor.getLocale().toLocale();
                        es.sendPatientAppointmentReminderEmail(patient, doctor, appointment, shift, patientLocale);
                        es.sendDoctorAppointmentReminderEmail(patient, doctor, appointment, shift, doctorLocale);
                        LOGGER.info("Sending appointment reminder email to patient {} and doctor {} for appointment on {}", patient.getId(), doctor.getId(), date);
                    });
                }
            });
        }
        LOGGER.info("Tomorrow appointments reminder sent. At " + LocalDateTime.now().toLocalTime());
    }
            
}