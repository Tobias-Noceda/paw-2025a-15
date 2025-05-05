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
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
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

    private final EmailService es;

    private final UserService us;

    private final DoctorShiftService dss;

    private final DoctorDetailService dds;

    private final AppointmentDao appointmentDao;

    @Autowired
    public AppointmentServiceImpl(final AppointmentDao appointmentDao, final EmailService es, final UserService us, final DoctorShiftService dss, final DoctorDetailService dds){
        this.appointmentDao = appointmentDao;
        this.es = es;
        this.us = us;
        this.dss = dss;
        this.dds = dds;
    }

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
            
            if(!dds.hasAuthDoctor(patientId, doctor.getId())) {
                dds.toggleAuthDoctor(patientId, doctor.getId());//grant doctors access to the patient profile
            }
        }
        
        return appointment;
    }

    @Override
    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date) {
        return appointmentDao.getAppointmentsByShiftIdAndDate(shiftId, date);
    }

    @Override
    public List<AppointmentData> getFutureAppointmentDataByPatientId(long patientId) {
        return appointmentDao.getFutureAppointmentDataByPatientId(patientId);
    }

    @Override
    public List<AppointmentData> getOldAppointmentDataByPatientId(long patientId) {
        return appointmentDao.getOldAppointmentDataByPatientId(patientId);
    }

    @Override
    public List<AppointmentData> getFutureAppointmentDataByDoctorId(long doctorId) {
        return appointmentDao.getFutureAppointmentDataByDoctorId(doctorId);
    }

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

    @Override
    public void removeAppointment(long shiftId, LocalDate date, long doctorId) {
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        if(shift.getDoctorId() != doctorId) throw new IllegalArgumentException("User not authorized to remove this appointment");

        addAppointment(shiftId, doctorId, date);
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Argentina/Buenos_Aires")
    public void clearRemovedAppointmentBeforeDate() {
        LocalDate date = LocalDate.now().minusDays(1);
        appointmentDao.clearRemovedAppointmentBeforeDate(date);
        LOGGER.info("Removed appointments before " + date + " cleared. At " + LocalDateTime.now().toLocalTime());
    }
//DEFAULT: 0 0 7 * * *
    //cada 10s : */10 * * * * *
    @Scheduled(cron = "*/10 * * * * *", zone = "America/Argentina/Buenos_Aires")
    public void rememberTomorrowAppointments() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<Appointment> appointments = appointmentDao.getAppointmentsForDate(date);
        for (Appointment appointment : appointments) {
            us.getUserById(appointment.getPatientId()).ifPresent(patient -> {
                DoctorShift shift = dss.getShiftById(appointment.getShiftId()).orElse(null);
                if (shift != null) {
                    us.getUserById(shift.getDoctorId()).ifPresent(doctor -> {
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