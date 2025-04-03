package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.WeekdayEnum;

public interface DoctorShiftService {
    public DoctorShift create(long doctorId, WeekdayEnum weekday, String address, int amount, String range);

    public Optional<DoctorShift> getShiftById(long id);

    public List<DoctorShift> getShiftsByDoctorId(long doctorId);

    public List<DoctorShift> getShiftsByDoctorIdAndWeekday(long doctorId, WeekdayEnum weekday);

    public List<AvailableTurn> getAvailableTurnsByDoctorIdAndDate(long doctorId, LocalDate date);

    public List<AvailableTurn> getAvailableTurnsByDoctorIdBetweenDates(long doctorId, LocalDate startDate, LocalDate endDate);

    public List<AvailableTurn> getAvailableTurnsByDoctorIdByMonth(long doctorId, Month month);
}
