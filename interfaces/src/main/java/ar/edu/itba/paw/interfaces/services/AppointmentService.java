package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.models.utils.Pair;

public interface AppointmentService {
    public AppointmentNew addAppointment(long shiftId, long patientId, LocalDate date, LocalTime startTime, LocalTime endTime, String detail);

    public Pair<AppointmentNew, AppointmentStatusEnum> getAppointmentByShiftIdDateAndTime(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime);

    public List<AppointmentNew> getOldAppointmentDataByPatientId(long patientId);

    public List<AppointmentNew> getOldAppointmentDataPageByPatientId(long patientId, int page, int pageSize);

    public Integer getOldAppointmentTotalByPatientId(long patientId);

    public List<AppointmentNew> getFutureAppointmentDataByDoctorId(long doctorId);

    public List<AppointmentNew> getFutureAppointmentDataPageByUserId(long userId, int page, int pageSize);

    public Integer getFutureAppointmentTotalByUserId(long userId);

    public AppointmentNew cancelAppointment(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime, long cancellerId);

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
