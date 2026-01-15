package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.AppointmentNew;

public interface AppointmentService {
    public AppointmentNew addAppointment(long shiftId, long patientId, LocalDate date, LocalTime startTime, LocalTime endTime, String detail);

    public Optional<AppointmentNew> getAppointmentByShiftIdDateAndTime(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime);

    public List<AppointmentNew> getFutureAppointmentDataByPatientId(long patientId);

    public List<AppointmentNew> getOldAppointmentDataByPatientId(long patientId);

    public List<AppointmentNew> getFutureAppointmentDataByDoctorId(long doctorId);

    public void cancelAppointment(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime, long cancellerId);

    public void removeAppointment(long shiftId, LocalDate date, long doctorId, LocalTime startTime, LocalTime endTime);

    public  void cancelAppointmentRange(long doctorId, LocalDate startDate, LocalDate endDate);

    /**
     * * Returns a list of available turns for a doctor in a specific date.
     * * @param doctorId The ID of the doctor.
     * * @return A list of available turns for the specified doctor and date.
     * *         If no turns are available, an empty list is returned.
     */
    public List<AppointmentNew> getAvailableTurnsByDoctorIdByDate(long doctorId, LocalDate date);
}
