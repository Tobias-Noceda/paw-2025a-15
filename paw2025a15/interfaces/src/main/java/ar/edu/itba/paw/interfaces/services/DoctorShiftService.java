package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.WeekdayEnum;

public interface DoctorShiftService {
    public DoctorShift create(long doctorId, WeekdayEnum weekday, String address, int amount, String range);

    public Optional<DoctorShift> getShiftById(long id);

    public List<DoctorShift> getShiftsByDoctorId(long doctorId);
}
