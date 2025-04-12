package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;

public interface AppointmentService {
    public Appointment addAppointment(long shiftId, long patientId, LocalDate date);

    public List<Appointment> getAppointmentsByShiftId(long shiftId);

    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date);

    public List<Appointment> getAppointmentsByPatientId(long patientId);

    public List<AppointmentData> getAppointmentDataByPatientId(long patientId);

    public List<AppointmentData> getAppointmentDataByDoctorId(long doctorId);

    public void cancelAppointment(long shiftId, LocalDate date, long cancelId);
}
