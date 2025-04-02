package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.DoctorShift;

public interface DoctorShiftDao {
    public DoctorShift create(long doctorId, String weekday, String address, int amount, String range);

    public Optional<DoctorShift> getShiftById(long id);

    public List<DoctorShift> getShiftsByDoctorId(long doctorId);
}
