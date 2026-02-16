package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.models.exceptions.AppointmentAlreadyTakenException;
import ar.edu.itba.paw.models.exceptions.BadRequestException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;
import ar.edu.itba.paw.models.utils.Pair;

@Service
public class AppointmentServiceImpl implements AppointmentService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private EmailService es;

    @Autowired
    private PatientService ps;

    @Autowired
    private DoctorService ds;

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private AuthDoctorService ads;

    @Transactional
    @Override
    public AppointmentNew addAppointment(long shiftId, long patientId, LocalDate date, LocalTime startTime, LocalTime endTime, String detail) {
        DoctorSingleShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with id: " + shiftId + " not found"));
        Doctor doctor = ds.getDoctorById(shift.getDoctor().getId()).orElseThrow(() -> new NotFoundException("Doctor with id: " + shift.getDoctor().getId() + " does not exist!"));
        User user;
        if(patientId != doctor.getId()) {
            user = (Patient) ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        } else {
            user = doctor;
        }

        if(date == null || date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && startTime.isBefore(LocalTime.now()))) throw new IllegalArgumentException("Shift must be in a valid datetime");
        
        if(date.getDayOfWeek().ordinal() != shift.getWeekday().ordinal()) throw new IllegalArgumentException("Shift must be on the same day of the week as the appointment date");
        
        if (!getAppointmentByShiftIdDateAndTime(shiftId, date, startTime, endTime).right().equals(AppointmentStatusEnum.FREE)) {
            LOGGER.error("Attempted to create an already taken appointment for patientId: {}, shiftId: {} and date: {} at {}", patientId, shiftId, date, LocalDateTime.now());
            throw new AppointmentAlreadyTakenException("Appointment for shiftId: " + shiftId +", date: " + date +" at " + startTime + " is already taken");
        }

        AppointmentNew appointment = appointmentDao.addAppointment(shiftId, patientId, date, startTime, endTime, detail);
        if(appointment == null){
            LOGGER.error("Failed to create appointment for patientId: {}, shiftId: {} and date: {} at {}", patientId, shiftId, date, LocalDateTime.now());
            throw new RuntimeException("Failed to create appointment for patientId: " + patientId +", shiftId: " + shiftId +" and date: " + date);
        }
        LOGGER.info("Successfully created appointment for patientId: {}, shiftId: {} and date: {}", patientId, shiftId, date);
        if(!user.getId().equals(doctor.getId())) {
            es.sendDoctorTakenShiftEmail((Patient) user, doctor, appointment, shift);
            es.sendPatientTakenShiftEmail((Patient) user, doctor, appointment, shift);
            
            if(!ads.hasAuthDoctor(patientId, doctor.getId())) {
                ads.toggleAuthDoctor(patientId, doctor.getId());//grant doctors access to the patient profile
            }
        }
        return appointment;
    }

    @Transactional(readOnly = true)
    @Override
    public Pair<AppointmentNew, AppointmentStatusEnum> getAppointmentByShiftIdDateAndTime(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        DoctorSingleShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with id: " + shiftId + " does not exist!"));
        
        AppointmentNew appointment = appointmentDao.getAppointmentByShiftDateAndTime(shift, date, startTime, endTime).orElse(null);
        AppointmentStatusEnum status = date.isAfter(LocalDate.now()) || (date.isEqual(LocalDate.now()) && startTime.isAfter(LocalTime.now())) ? AppointmentStatusEnum.TAKEN : AppointmentStatusEnum.COMPLETED;


        if(appointment == null && shift.isValidDate(date) && shift.isValidStartAndEndTime(startTime, endTime)) {
            appointment = new AppointmentNew(shift, null, date, startTime, endTime, null);
            status = AppointmentStatusEnum.FREE;
        }
        
        if (appointment != null) {
            return new Pair<>(appointment, status);
        }

        throw new NotFoundException("No appointment found for shiftId: " + shiftId + ", date: " + date + ", startTime: " + startTime + " and endTime: " + endTime);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentNew> getOldAppointmentDataPageByPatientId(long patientId, int page, int pageSize) {
        Patient patient = ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return appointmentDao.getOldAppointmentDataPageByPatient(patient, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getOldAppointmentTotalByPatientId(long patientId) {
        Patient patient = ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return appointmentDao.getOldAppointmentTotalByPatient(patient);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentNew> getFutureAppointmentDataByDoctorId(long doctorId) {
        Doctor doctor = ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return appointmentDao.getFutureAppointmentDataByDoctor(doctor);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentNew> getFutureAppointmentDataPageByUserId(long userId, int page, int pageSize) {
        Doctor doctor = ds.getDoctorById(userId).orElse(null);

        if (doctor != null) {
            return appointmentDao.getFutureAppointmentDataPageByDoctor(doctor, page, pageSize);
        } else {
            Patient patient = ps.getPatientById(userId).orElseThrow(() -> new NotFoundException("User with id: " + userId + " does not exist!"));
            return appointmentDao.getFutureAppointmentDataPageByPatient(patient, page, pageSize);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getFutureAppointmentTotalByUserId(long userId) {
        Doctor doctor = ds.getDoctorById(userId).orElse(null);

        if (doctor != null) {
            return appointmentDao.getFutureAppointmentTotalByDoctor(doctor);
        } else {
            Patient patient = ps.getPatientById(userId).orElseThrow(() -> new NotFoundException("User with id: " + userId + " does not exist!"));
            return appointmentDao.getFutureAppointmentTotalByPatient(patient);
        }
    }

    @Transactional
    @Override
    public AppointmentNew cancelAppointment(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime, long cancellerId) {
        DoctorSingleShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with shiftId: " + shiftId + " does not exist!"));
        Pair<AppointmentNew, AppointmentStatusEnum> appointmentPair = getAppointmentByShiftIdDateAndTime(shiftId, date, startTime, endTime);

        if (appointmentPair.right().equals(AppointmentStatusEnum.FREE) || appointmentPair.right().equals(AppointmentStatusEnum.COMPLETED)) {
            throw new BadRequestException("Cannot cancel a FREE or COMPLETED appointments");
        }

        AppointmentNew appointment = appointmentPair.left();

        Patient patient = ps.getPatientById(appointment.getPatient().getId()).orElseThrow(() -> new NotFoundException("Patient with id: " + appointment.getPatient().getId() + " does not exist!"));
        Doctor doctor = ds.getDoctorById(shift.getDoctor().getId()).orElseThrow(() -> new NotFoundException("Doctor with id: " + shift.getDoctor().getId() + " does not exist!"));

        if (patient.getId().equals(cancellerId)) {
            if(appointmentDao.cancelAppointment(shift, date, startTime, endTime)){
                LOGGER.info("Patient with id: {} has cancelled their appointment with doctor with id: {} on the shiftId:{} and date {}", patient.getId(), doctor.getId(), shiftId, date);
                es.sendPatientCancellationConfirmationEmail(patient, doctor, appointment, shift);
                es.sendDoctorCancelledAppointmentEmail(patient, doctor, appointment, shift);
            }
        } else if (doctor.getId().equals(cancellerId)) {
            if(appointmentDao.cancelAppointment(shift, date, startTime, endTime)){
                LOGGER.info("Doctor with id: {} has cancelled their appointment with patient with id: {} on the shiftId:{} and date {}", doctor.getId(), patient.getId(), shiftId, date);
                es.sendDoctorCancellationConfirmationEmail(patient, doctor, appointment, shift);
                es.sendPatientCancelledAppointmentEmail(patient, doctor, appointment, shift);
            }
        } else throw new UnauthorizedException("User not authorized to cancel this appointment");

        return appointment.free();
    }

    @Transactional
    @Override
    public void removeAppointment(long shiftId, LocalDate date, long doctorId, LocalTime startTime, LocalTime endTime) {
        DoctorSingleShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new NotFoundException("Shift with shiftId: " + shiftId + " does not exist!"));
        if(shift.getDoctor().getId() != doctorId) throw new UnauthorizedException("User not authorized to remove this appointment");

        addAppointment(shiftId, doctorId, date, startTime, endTime, null);//recreate the appointment with null detail to remove it from the free appointments list
        LOGGER.info("Doctor with id: {} has removed an appointment from their free appointments at shiftId: {} and date: {}", doctorId, shiftId, date);
    }

    @Transactional
    @Override
    public void  cancelAppointmentRange(long doctorId, LocalDate startDate, LocalDate endDate) {
        List<AppointmentNew> cancelled = appointmentDao.cancelAppointmentRange(doctorId,startDate, endDate);
        LOGGER.info("Cancelled {} appointments for doctor with id: {}", cancelled.size(), doctorId);

        for (AppointmentNew appointment : cancelled) {
            es.sendPatientCancelledAppointmentEmail((Patient) appointment.getPatient(), appointment.getShift().getDoctor(), appointment, appointment.getShift());
        }
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
          
    
    @Transactional(readOnly = true)
    @Override
    public List<AppointmentNew> getAvailableTurnsByDoctorIdByDate(long doctorId, LocalDate date) {
        Doctor doctor = ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return appointmentDao.getAvailableTurnsByDoctorByDate(doctor, date);
    }
}