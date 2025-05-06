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
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;

@Service
public class AppointmentServiceImpl implements AppointmentService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private  EmailService es;

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
        User patient = us.getUserById(patientId).orElseThrow(() -> new IllegalArgumentException("No such patient"));
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        User doctor = us.getUserById(shift.getDoctorId()).orElseThrow(() -> new IllegalArgumentException("No such doctor"));
        if(date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && shift.getStartTime().isBefore(LocalTime.now()))) throw new IllegalArgumentException("Shift must be in a valid datetime");
        
        // TODO: cambiar a usar el enum de java.time en vez del nuestro para poder comparar
        if(date.getDayOfWeek().ordinal() != shift.getWeekday().ordinal()) throw new IllegalArgumentException("Shift must be on the same day of the week as the appointment date");
        
        getAppointmentsByShiftIdAndDate(shiftId, date).ifPresent(a -> {throw new IllegalArgumentException("Shift already taken");});

        Appointment appointment = appointmentDao.addAppointment(shiftId, patientId, date);
        if(patient.getId() != doctor.getId() && appointment != null) {
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
        Appointment appointment = getAppointmentsByShiftIdAndDate(shiftId, date).orElseThrow(() -> new IllegalArgumentException("No such appointment"));
        User patient = us.getUserById(appointment.getPatientId()).orElseThrow(() -> new IllegalArgumentException("No such patient"));
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        User doctor = us.getUserById(shift.getDoctorId()).orElseThrow(() -> new IllegalArgumentException("No such doctor"));

        if(cancelId==patient.getId()){
            if(appointmentDao.removeAppointment(shiftId, date)){
                es.sendPatientCancellationConfirmationEmail(patient, doctor, appointment, shift);
                es.sendDoctorCancelledAppointmentEmail(patient, doctor, appointment, shift);
            }
        } else if(cancelId==doctor.getId()) {
            if(appointmentDao.removeAppointment(shiftId, date)){
                es.sendDoctorCancellationConfirmationEmail(patient, doctor, appointment, shift);
                es.sendPatientCancelledAppointmentEmail(patient, doctor, appointment, shift);
            }
        } else throw new IllegalArgumentException("User not authorized to cancel this appointment");
    }

    @Transactional
    @Override
    public void removeAppointment(long shiftId, LocalDate date, long doctorId) {
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        if(shift.getDoctorId() != doctorId) throw new IllegalArgumentException("User not authorized to remove this appointment");

        addAppointment(shiftId, doctorId, date);
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * *", zone = "America/Argentina/Buenos_Aires")
    public void clearRemovedAppointmentBeforeDate() {
        LocalDate date = LocalDate.now().minusDays(1);
        appointmentDao.clearRemovedAppointmentBeforeDate(date);
        LOGGER.info("Removed appointments before " + date + " cleared. At " + LocalDateTime.now().toLocalTime());
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 7 * * *", zone = "America/Argentina/Buenos_Aires")
    public void rememberTomorrowAppointments() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<Appointment> appointments = appointmentDao.getAppointmentsForDate(date);
        for(Appointment appointment : appointments) {
            us.getUserById(appointment.getPatientId()).ifPresent(patient -> {
                LOGGER.info("Sending appointment reminder email to patient " + patient.getId() + " for appointment " + LocalDate.now());
                // TODO: Email
                // es.sendAppointmentRemainderEmail(appointment);
            });
        }
        LOGGER.info("Tomorrow appointments reminder sent. At " + LocalDateTime.now().toLocalTime());
    }
            
}