package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

import ar.edu.itba.paw.models.Insurance;

public interface DoctorCoverageDao {
    public void addCoverage(long doctorId, long insuranceId);

    public boolean removeCoverage(long doctorId, long insuranceId);

    public List<Insurance> getInsurancesById(long doctorId);
}
