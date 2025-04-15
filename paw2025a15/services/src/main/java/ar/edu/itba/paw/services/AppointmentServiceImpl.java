package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private final EmailService es;

    private final UserService us;

    private final DoctorShiftService dss;

    private final UserService us;

    private final DoctorShiftService dss;

    private final AppointmentDao appointmentDao;

    @Autowired
    public AppointmentServiceImpl(final AppointmentDao appointmentDao, final EmailService es, final UserService us, final DoctorShiftService dss){
        this.appointmentDao = appointmentDao;
        this.es = es;
        this.us = us;
        this.dss = dss;
        this.us = us;
        this.dss = dss;
    }

    @Override
    public Appointment addAppointment(long shiftId, long patientId, LocalDate date) {
        User patient = us.getUserById(patientId).orElseThrow(() -> new IllegalArgumentException("No such patient"));
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        User doctor = us.getUserById(shift.getDoctorId()).orElseThrow(() -> new IllegalArgumentException("No such doctor"));
        if(date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && shift.getStartTime().isBefore(LocalTime.now()))) throw new IllegalArgumentException("Shift must be in a valid datetime");
        Appointment appointment = appointmentDao.addAppointment(shiftId, patientId, date);
        es.sendTakenShiftEmail(patient, doctor, appointment, shift);
        return appointment;
    }

    @Override
    public List<Appointment> getAppointmentsByShiftId(long shiftId) {
        return appointmentDao.getAppointmentsByShiftId(shiftId);
    }

    @Override
    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date) {
        return appointmentDao.getAppointmentsByShiftIdAndDate(shiftId, date);
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(long patientId) {
        return appointmentDao.getAppointmentsByPatientId(patientId);
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
    public List<AppointmentData> getOldAppointmentDataByDoctorId(long doctorId) {
        return appointmentDao.getOldAppointmentDataByDoctorId(doctorId);
    }

    @Override
    public void cancelAppointment(long shiftId, LocalDate date, long cancelId) {
        Appointment appointment = getAppointmentsByShiftIdAndDate(shiftId, date).orElseThrow(()->new IllegalArgumentException("No such appointment"));
        User patient = us.getUserById(appointment.getPatientId()).orElseThrow(()->new IllegalArgumentException("No such patient"));
        DoctorShift shift = dss.getShiftById(shiftId).orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        User doctor = us.getUserById(shift.getDoctorId()).orElseThrow(()->new IllegalArgumentException("No such doctor"));
        if(appointmentDao.removeAppointment(shiftId, date)){
            if(cancelId==patient.getId()) es.sendPatientCancellationEmails(patient, doctor, appointment, shift);
            else if(cancelId==doctor.getId()) es.sendDoctorCancellationEmails(patient, doctor, appointment, shift);
        }
    }

}
