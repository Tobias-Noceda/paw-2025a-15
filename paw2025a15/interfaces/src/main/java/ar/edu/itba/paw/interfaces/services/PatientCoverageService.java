package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.Insurance;

public interface PatientCoverageService {
    public void addCoverage(long patientId, long insuranceId);

    public boolean removeCoverage(long patientId, long insuranceId);

    public Optional<Insurance> getInsuranceById(long patientId);
}
