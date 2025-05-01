package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;

public interface AppointmentDao {
    public Appointment addAppointment(long shiftId, long patientId, LocalDate date);

    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date);

    public List<AppointmentData> getFutureAppointmentDataByPatientId(long patientId);

    public List<AppointmentData> getOldAppointmentDataByPatientId(long patientId);

    public List<AppointmentData> getFutureAppointmentDataByDoctorId(long doctorId);

    public boolean removeAppointment(long shiftId, LocalDate date);

    public void clearRemovedAppointmentBeforeDate(LocalDate date);
}
