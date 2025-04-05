package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.DoctorDetail;

public interface DoctorDetailService {
    public DoctorDetail create(long doctorId, String licence, String specialty);

    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId);
}
