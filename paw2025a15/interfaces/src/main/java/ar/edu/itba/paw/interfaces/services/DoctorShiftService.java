package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.WeekdayEnum;

public interface DoctorShiftService {
    public DoctorShift create(long doctorId, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime);

    public void createShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int amount);

    public Optional<DoctorShift> getShiftById(long id);

    public List<DoctorShift> getShiftsByDoctorId(long doctorId);

    public List<DoctorShift> getUnifiedShiftsByDoctorId(long doctorId);

    public List<DoctorShift> getShiftsByDoctorIdAndWeekday(long doctorId, WeekdayEnum weekday);

    public List<DoctorShift> getShiftsByDoctorIdAndWeekdayAndStartTime(long doctorId, WeekdayEnum weekday, LocalTime startTime);

    public List<DoctorShift> getAvailableShiftsByDoctorIdWeekdayAndDate(long doctorId, WeekdayEnum weekday, LocalDate date);

    public List<DoctorShift> getAvailableShiftsByDoctorIdWeekdayAndDateTime(long doctorId, WeekdayEnum weekday, LocalDate date, LocalTime time);

    public List<AvailableTurn> getAvailableTurnsByDoctorIdAndDate(long doctorId, LocalDate date);

    public List<AvailableTurn> getAvailableTurnsByDoctorIdBetweenDates(long doctorId, LocalDate startDate, LocalDate endDate);

    public List<AvailableTurn> getAvailableTurnsByDoctorIdByMonth(long doctorId, Month month);

    /**
     * * Returns a list of available turns for a doctor in a specific month and week number.
     * * @param doctorId The ID of the doctor.
     * * @param month The month for which to retrieve available turns.
     * * @param weekNumber The week number of the month (0-3). 
     *                     * 0 = first week (1-7),
     *                     * 1 = second week (8-14),
     *                     * 2 = third week (15-21),
     *                     * 3 = fourth week (22-end).
     * * @return A list of available turns for the specified doctor, month, and week number.
     * *         If no turns are available, an empty list is returned.
     */
    public List<AvailableTurn> getAvailableTurnsByDoctorIdByMonthAndWeekNumber(long doctorId, Month month, int weekNumber);
}
