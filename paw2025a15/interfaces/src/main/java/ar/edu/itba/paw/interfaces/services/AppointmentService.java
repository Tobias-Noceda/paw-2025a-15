package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.util.List;

import ar.edu.itba.paw.models.Appointment;

public interface AppointmentService {
    public void addApointment(long shiftId, long patientId, LocalDate date);

    public List<Appointment> getAppointmentsByShiftId(long shiftId);

    public List<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date);//TODO ya no es list es un Optional

    public List<Appointment> getAppointmentsByPatientId(long patientId);
}
