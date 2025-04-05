package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.models.Appointment;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentDao appointmentDao;

    @Autowired
    public AppointmentServiceImpl(final AppointmentDao appointmentDao){
        this.appointmentDao = appointmentDao;
    }

    @Override
    public void addApointment(long shiftId, long patientId, int idx, LocalDate date) {
        appointmentDao.addApointment(shiftId, patientId, idx, date);
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
    public List<Integer> getAppointmentIdxByShiftAndDate(long shiftId, LocalDate date) {
        return appointmentDao.getAppointmentIdxByShiftAndDate(shiftId, date);
    }

}
