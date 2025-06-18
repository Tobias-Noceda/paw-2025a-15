package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorShiftService {
    public void createShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int amount);

    public Optional<DoctorSingleShift> getShiftById(long id);

    public void updateShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int amount, boolean keepTurns);

    /**
     * * Returns a list of available turns for a doctor in a specific date.
     * * @param doctorId The ID of the doctor.
     * * @return A list of available turns for the specified doctor and date.
     * *         If no turns are available, an empty list is returned.
     */
    public List<AvailableTurn> getAvailableTurnsByDoctorIdByDate(long doctorId, LocalDate date);
}
