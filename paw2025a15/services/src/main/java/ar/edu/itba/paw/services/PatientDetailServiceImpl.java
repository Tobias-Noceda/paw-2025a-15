package ar.edu.itba.paw.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;

@Service
public class PatientDetailServiceImpl implements PatientDetailService{

    @Autowired
    private PatientDetailDao patientDetailDao;

    @Transactional
    @Override
    public PatientDetail create(long patientId, Integer age, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        //TODO:refactor so all patient functions are here and not in user (otherwise circular reference)
        //TODO: aca va un if(us.getUserById(patientId).isPresent())
        return patientDetailDao.create(patientId, age, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PatientDetail> getDetailByPatientId(long patientId) {
        return patientDetailDao.getDetailByPatientId(patientId);
    }

    @Transactional
    @Override
    public void updatePatientDetails(long patientId, Integer age, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        if(getDetailByPatientId(patientId).isPresent()) patientDetailDao.updatePatientDetails(patientId, age, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
    }
    
}
