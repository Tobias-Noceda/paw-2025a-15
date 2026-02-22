package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorShiftDao {//TODO check deprecated
    public Optional<DoctorSingleShift> getShiftById(long id);

    @Deprecated
    public DoctorSingleShift create(Doctor doctor, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime, int slot);

    public void doctorSetShifts(Doctor doctor, List<DoctorSingleShift> shifts);

    public void updateShifts(long doctorId, List<DoctorSingleShift> newShifts);

    public List<DoctorSingleShift> getActiveShiftsByDoctorId(long doctorId);//TODO deprecar por los paginados

    public List<DoctorSingleShift> getActiveShiftsByDoctorIdPage(long doctorId, int page, int pageSize);
    
    public int getActiveShiftsByDoctorIdCount(long doctorId);
}
