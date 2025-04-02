package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

import ar.edu.itba.paw.models.DoctorDetail;

public interface DoctorDetailDao {
    public DoctorDetail create(long doctorId, String licence, String specialty);

    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId);
}
