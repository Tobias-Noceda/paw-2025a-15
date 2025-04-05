package ar.edu.itba.paw.interfaces.services;

import java.util.List;

import ar.edu.itba.paw.models.Insurance;

public interface PatientCoverageService {
    public void addCoverage(long patientId, long insuranceId);

    public boolean removeCoverage(long patientId, long insuranceId);

    public List<Insurance> getInsurancesById(long patientId);
}
