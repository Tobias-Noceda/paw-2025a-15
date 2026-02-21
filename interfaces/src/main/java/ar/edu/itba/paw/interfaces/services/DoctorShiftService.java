package ar.edu.itba.paw.interfaces.services;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorShiftService {
    public void createShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int amount);

    public Optional<DoctorSingleShift> getShiftById(long id);

    public void updateShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int amount, boolean keepTurns);

    public List<DoctorSingleShift> getActiveShiftsByDoctorId(long doctorId);

    @Deprecated
    public List<DoctorSingleShift> getActiveShiftsByDoctorIdPage(long doctorId, int page, int pageSize);
    
    @Deprecated
    public int getActiveShiftsByDoctorIdCount(long doctorId);
}
