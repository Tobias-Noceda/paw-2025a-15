package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorShiftDao {
    public DoctorShift create(User doctor, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime);

    public int[] batchCreate(List<DoctorShift> shifts);

    public Optional<DoctorShift> getShiftById(long id);

    public List<DoctorShift> getShiftsByDoctorId(long doctorId);

    public List <AvailableTurn> getAvailableTurnsByDoctorIdBetweenDates(long doctorId, LocalDate startDate, LocalDate endDate);
    
    public List<DoctorShift> getAvailableShiftsByDoctorIdWeekdayAndDateTime(long doctorId, WeekdayEnum weekday, LocalDate date, LocalTime time);
}
