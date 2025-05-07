package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;

@Service
public class PatientDetailsServiceImpl implements PatientDetailService{

    @Autowired
    private PatientDetailDao patientDetailDao;

    @Override
    public PatientDetail create(long patientId, LocalDate birthdate, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        return patientDetailDao.create(patientId, birthdate, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
    }

    @Override
    public Optional<PatientDetail> getDetailByPatientId(long patientId) {
        return patientDetailDao.getDetailByPatientId(patientId);
    }

    @Override
    public void updatePatientDetails(long patientId, LocalDate birthdate, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        patientDetailDao.updatePatientDetails(patientId, birthdate, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
    }
    
}
