package ar.edu.itba.paw.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.DoctorCoverageDao;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.models.Insurance;

@Service
public class DoctorCoverageServiceImpl implements DoctorCoverageService{

    @Autowired
    private DoctorCoverageDao doctorCoverageDao;

    @Transactional
    @Override
    public void setCoverages(long doctorId, List<Long> insurances) {
        List<Insurance> currentInsurances = doctorCoverageDao.getInsurancesById(doctorId);
        if(currentInsurances != null && !currentInsurances.isEmpty()) { 
            for (Insurance insurance : currentInsurances) {
                if (!insurances.contains(insurance.getId())) {
                    doctorCoverageDao.removeCoverage(doctorId, insurance.getId());
                } else {
                    insurances.remove(insurance.getId());
                }
            }
        }
        for (Long insuranceId : insurances) {
            doctorCoverageDao.addCoverage(doctorId, insuranceId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Insurance> getInsurancesById(long doctorId) {
        return doctorCoverageDao.getInsurancesById(doctorId);
    }

}
