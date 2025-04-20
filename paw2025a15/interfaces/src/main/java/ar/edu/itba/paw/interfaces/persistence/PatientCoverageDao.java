package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

import ar.edu.itba.paw.models.Insurance;


public interface PatientCoverageDao {
    public void addCoverage(long patientId, long insuranceId);

    public boolean removeCoverage(long patientId, long insuranceId);

    public Optional<Insurance> getInsuranceById(long patientId);
}