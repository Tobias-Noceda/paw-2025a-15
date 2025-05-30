package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.PatientDetail;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class PatientDetailServiceImpl implements PatientDetailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDetailServiceImpl.class);

    @Autowired
    private PatientDetailDao patientDetailDao;

    @Autowired
    private UserService us;

    @Autowired
    private FileService fs;

    @Transactional
    @Override//TODO: esta rari que no llame al dao
    public User createPatient(String email, String password, String name, String telephone, LocaleEnum locale) {
        if(us.getUserByEmail(email).isPresent()) throw new AlreadyExistsException("User with email: " + email + " already exists!");
        File picture = fs.findById(1).orElseThrow(() -> new NotFoundException("Default picture not found!"));
        return us.createPatient(email, password, name, telephone, picture, locale, LocalDate.now(), null, null);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PatientDetail> getDetailByPatientId(long patientId) {
        return patientDetailDao.getDetailByPatientId(patientId);
    }

    @Transactional
    @Override
    public void updatePatientDetails(long patientId, LocalDate birthdate, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        if(getDetailByPatientId(patientId).isEmpty()) throw new NotFoundException("Patient details for userId: " + patientId + " does not exist!");
        patientDetailDao.updatePatientDetails(patientId, birthdate, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
        LOGGER.info("Edited patient details for userId: {}", patientId);
    }
    
}