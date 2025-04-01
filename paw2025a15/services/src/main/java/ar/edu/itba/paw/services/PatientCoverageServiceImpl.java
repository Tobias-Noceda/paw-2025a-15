package ar.edu.itba.paw.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.PatientCoverageDao;
import ar.edu.itba.paw.interfaces.services.PatientCoverageService;
import ar.edu.itba.paw.models.Insurance;

@Service
public class PatientCoverageServiceImpl implements PatientCoverageService{

    private final PatientCoverageDao patientCoverageDao;

    @Autowired
    public PatientCoverageServiceImpl(final PatientCoverageDao patientCoverageDao){
        this.patientCoverageDao = patientCoverageDao;
    }

    @Override
    public void addCoverage(long patientId, long insuranceId) {
        patientCoverageDao.addCoverage(patientId, insuranceId);
    }

    @Override
    public boolean removeCoverage(long patientId, long insuranceId) {
        return patientCoverageDao.removeCoverage(patientId, insuranceId);
    }

    @Override
    public List<Insurance> getInsurancesById(long patientId) {
        return patientCoverageDao.getInsurancesById(patientId);
    }

}
