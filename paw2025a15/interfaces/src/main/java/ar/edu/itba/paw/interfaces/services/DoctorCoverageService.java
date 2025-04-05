package ar.edu.itba.paw.interfaces.services;

import java.util.List;

import ar.edu.itba.paw.models.Insurance;

public interface DoctorCoverageService {
    public void addCoverage(long doctorId, long insuranceId);

    public void addCoverages(long doctorId, List<Long> insurances);

    public boolean removeCoverage(long doctorId, long insuranceId);

    public List<Insurance> getInsurancesById(long doctorId);
}
