package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.AppointmentData;
import ar.edu.itba.paw.models.entities.Appointment;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.User;

public interface AppointmentDao {
    public Appointment addAppointment(DoctorShift shift, User patient, LocalDate date);

    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date);

    public List<AppointmentData> getFutureAppointmentDataByPatientId(long patientId);

    public List<AppointmentData> getOldAppointmentDataByPatientId(long patientId);

    public List<AppointmentData> getFutureAppointmentDataByDoctorId(long doctorId);

    public List<Appointment> getAppointmentsForDate(LocalDate date);

    public boolean removeAppointment(long shiftId, LocalDate date);

    public void clearRemovedAppointmentBeforeDate(LocalDate date);
}
