package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Appointment;

public interface AppointmentService {
    public Appointment addAppointment(long shiftId, long patientId, LocalDate date);

    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date);

    public List<AppointmentNew> getFutureAppointmentDataByPatientId(long patientId);

    public List<AppointmentNew> getOldAppointmentDataByPatientId(long patientId);

    public List<AppointmentNew> getFutureAppointmentDataByDoctorId(long doctorId);

    public void cancelAppointment(long shiftId, LocalDate date, long cancelId);

    public void removeAppointment(long shiftId, LocalDate date, long doctorId);
}
