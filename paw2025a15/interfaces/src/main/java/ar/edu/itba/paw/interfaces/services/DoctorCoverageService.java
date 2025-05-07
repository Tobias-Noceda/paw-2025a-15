package ar.edu.itba.paw.interfaces.services;

import java.util.List;

import ar.edu.itba.paw.models.Insurance;

public interface DoctorCoverageService {
    public void setCoverages(long doctorId, List<Long> insurances);

    public List<Insurance> getInsurancesById(long doctorId);
}
