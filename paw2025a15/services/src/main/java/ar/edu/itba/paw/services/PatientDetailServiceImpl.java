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
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@Service
public class PatientDetailServiceImpl implements PatientDetailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDetailServiceImpl.class);

    @Autowired
    private PatientDetailDao patientDetailDao;

    @Autowired
    private UserService us;

    @Transactional
    @Override
    public User createPatient(String email, String password, String name, String telephone, LocaleEnum locale) {
        if(us.getUserByEmail(email).isPresent()) throw new IllegalArgumentException("User with email: " + email + " already exists!");
        User patient = us.create(email, password, name, telephone, UserRoleEnum.PATIENT, locale);
        long patientId = patient.getId();
        PatientDetail pd = patientDetailDao.create(patient.getId(), null, null, null, null, null, null, null, null, null, null, null, null);
        if(pd == null){
            LOGGER.error("Failed to create patient details for userId: {} at {}", patientId, LocalDateTime.now());
            throw new RuntimeException("Failed to create patient details for userId: " + patientId);
        }
        LOGGER.info("Successfully created patient details for userId: {}", patientId);
        LOGGER.info("Successfully created patient user with email: {}", email);
        return patient;
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