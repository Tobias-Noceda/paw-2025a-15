package ar.edu.itba.paw.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.DoctorCoverageDao;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.models.Insurance;

@Service
public class DoctorCoverageServiceImpl implements DoctorCoverageService{

    private final DoctorCoverageDao doctorCoverageDao;

    @Autowired
    public DoctorCoverageServiceImpl(final DoctorCoverageDao doctorCoverageDao){
        this.doctorCoverageDao = doctorCoverageDao;
    }

    @Override
    public void addCoverage(long doctorId, long insuranceId) {
        doctorCoverageDao.addCoverage(doctorId, insuranceId);
    }

    @Override
    public void addCoverages(long doctorId, List<Insurance> insurances) {
        for (Insurance insurance : insurances) {
            addCoverage(doctorId, insurance.getId());
        }
    }

    @Override
    public boolean removeCoverage(long doctorId, long insuranceId) {
        return doctorCoverageDao.removeCoverage(doctorId, insuranceId);
    }

    @Override
    public List<Insurance> getInsurancesById(long doctorId) {
        return doctorCoverageDao.getInsurancesById(doctorId);
    }

}
