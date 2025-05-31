package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorShiftDao {
    public DoctorShift create(User doctor, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime);

    public int[] batchCreate(List<DoctorShift> shifts);

    public Optional<DoctorShift> getShiftById(long id);

    public List<DoctorShift> getShiftsByDoctorId(long doctorId);

    public List <AvailableTurn> getAvailableTurnsByDoctorIdByDate(long doctorId, LocalDate date);

    public List<DoctorShift> getAvailableShiftsByDoctorIdWeekdayAndDateTime(long doctorId, WeekdayEnum weekday, LocalDate date, LocalTime time);
}
