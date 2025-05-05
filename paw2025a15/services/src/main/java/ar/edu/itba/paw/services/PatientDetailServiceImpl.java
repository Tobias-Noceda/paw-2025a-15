package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;

@Service
public class PatientDetailServiceImpl implements PatientDetailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDetailServiceImpl.class);

    @Autowired
    private PatientDetailDao patientDetailDao;

    @Transactional
    @Override
    public PatientDetail create(long patientId, Integer age, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        //TODO:refactor so all patient functions are here and not in user (otherwise circular reference)
        //TODO: aca va un if(us.getUserById(patientId).isPresent())
        if(getDetailByPatientId(patientId).isPresent()) throw new IllegalArgumentException("Patient detail for userId: " + patientId + " already exists!");
        PatientDetail pd = patientDetailDao.create(patientId, age, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
        if(pd == null){
            LOGGER.error("Failed to create patient details for userId: {} at {}", patientId, LocalDateTime.now());
            throw new RuntimeException("Failed to create patient details for userId: " + patientId);
        }
        LOGGER.info("Successfully created patient details for userId: {}", patientId);
        return pd;
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
        if(!getDetailByPatientId(patientId).isPresent()) throw new IllegalArgumentException("Patient details for userId: " + patientId + " does not exist!");
        patientDetailDao.updatePatientDetails(patientId, age, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
        LOGGER.info("Edited patient details for userId: {}", patientId);
    }
    
}
