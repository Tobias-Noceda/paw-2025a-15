package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorShiftDao {
    public Optional<DoctorSingleShift> getShiftById(long id);

    public DoctorSingleShift create(Doctor doctor, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime, int slot);

    public void doctorSetShifts(Doctor doctor, List<DoctorSingleShift> shifts);

    public void updateShifts(long doctorId, List<DoctorSingleShift> newShifts);

    public List <AvailableTurn> getAvailableTurnsByDoctorByDate(Doctor doctor, LocalDate date);
}
