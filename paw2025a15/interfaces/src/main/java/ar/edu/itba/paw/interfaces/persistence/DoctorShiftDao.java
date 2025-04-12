package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.WeekdayEnum;

public interface DoctorShiftDao {
    public DoctorShift create(long doctorId, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime);

    public Optional<DoctorShift> getShiftById(long id);

    public List<DoctorShift> getShiftsByDoctorId(long doctorId);

    public List<DoctorShift> getShiftsByDoctorIdAndWeekday(long doctorId, WeekdayEnum weekday);

    public List<DoctorShift> getShiftsByDoctorIdAndWeekdayAndStartTime(long doctorId, WeekdayEnum weekday, LocalTime startTime);
}
