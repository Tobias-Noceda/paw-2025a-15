package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private final EmailService es;

    private final AppointmentDao appointmentDao;

    @Autowired
    public AppointmentServiceImpl(final AppointmentDao appointmentDao, final EmailService es){
        this.appointmentDao = appointmentDao;
        this.es = es;
    }

    @Override
    public Appointment addAppointment(long shiftId, long patientId, LocalDate date) {
        return appointmentDao.addAppointment(shiftId, patientId, date);
    }

    @Override
    public List<Appointment> getAppointmentsByShiftId(long shiftId) {
        return appointmentDao.getAppointmentsByShiftId(shiftId);
    }

    @Override
    public List<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date) {
        return appointmentDao.getAppointmentsByShiftIdAndDate(shiftId, date);
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(long patientId) {
        return appointmentDao.getAppointmentsByPatientId(patientId);
    }

    @Override
    public List<AppointmentData> getAppointmentDataByPatientId(long patientId) {
        return appointmentDao.getAppointmentDataByPatientId(patientId);
    }

}
