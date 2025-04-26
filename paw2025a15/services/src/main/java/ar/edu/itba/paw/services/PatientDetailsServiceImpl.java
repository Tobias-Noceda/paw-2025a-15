package ar.edu.itba.paw.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.BloodTypeEnum;
import ar.edu.itba.paw.models.PatientDetail;

@Service
public class PatientDetailsServiceImpl implements PatientDetailService{

    @Autowired
    private PatientDetailDao patientDetailDao;

    @Override
    public PatientDetail create(long patientId, Integer age, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        return patientDetailDao.create(patientId, age, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
    }

    @Override
    public Optional<PatientDetail> getDetailByPatientId(long patientId) {
        return patientDetailDao.getDetailByPatientId(patientId);
    }
    
}
