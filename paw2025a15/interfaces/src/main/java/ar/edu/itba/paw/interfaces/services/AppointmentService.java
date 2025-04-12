package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.util.List;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;

public interface AppointmentService {
    public Appointment addAppointment(long shiftId, long patientId, LocalDate date);

    public List<Appointment> getAppointmentsByShiftId(long shiftId);

    public List<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date);//TODO ya no es list es un Optional

    public List<Appointment> getAppointmentsByPatientId(long patientId);

    public List<AppointmentData> getAppointmentDataByPatientId(long patientId);
}
