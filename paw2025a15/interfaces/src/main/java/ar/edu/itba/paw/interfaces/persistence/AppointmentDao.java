package ar.edu.itba.paw.interfaces.persistence;

import java.util.Date;
import java.util.List;

import ar.edu.itba.paw.models.Appointment;

public interface AppointmentDao {
    public void addApointment(long shiftId, long patientId, int idx, Date date);

    public List<Appointment> getAppointmentsByShiftId(long shiftId);

    public List<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, Date date);

    public List<Appointment> getAppointmentsByPatientId(long patientId);
}
