package ar.edu.itba.paw.interfaces.services;

import java.util.Date;
import java.util.List;

import ar.edu.itba.paw.models.Appointment;

public interface AppointmentService {
    public void addApointment(long shiftId, long patientId, int idx, Date date);

    public List<Appointment> getAppointmentsByShiftId(long shiftId);

    public List<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, Date date);

    public List<Appointment> getAppointmentsByPatientId(long patientId);
}
