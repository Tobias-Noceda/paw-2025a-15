package ar.edu.itba.paw.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Optional<Patient> getPatientById(long id) {
        return patientDetailDao.getPatientById(id);
    }

    @Transactional
    @Override
    public Patient createPatient(String email, String password, String name, String telephone, LocaleEnum locale, LocalDate birthDate, BigDecimal height, BigDecimal weight) {
        if(us.getUserByEmail(email).isPresent()) throw new AlreadyExistsException("User with email: " + email + " already exists!");
        File picture = fs.findById(1).orElseThrow(() -> new NotFoundException("Default picture not found!"));
        return patientDetailDao.createPatient(email, passwordEncoder.encode(password), name, telephone, picture, locale, birthDate, height, weight);
    }

    @Transactional
    @Override
    public void updatePatient(
        Patient patient,
        String phoneNumber,
        File picture,
        LocaleEnum mailLanguage,
        LocalDate birthdate,
        BloodTypeEnum bloodType,
        BigDecimal height,
        BigDecimal weight,
        Boolean smokes,
        Boolean drinks,
        String meds,
        String conditions,
        String allergies,
        String diet,
        String hobbies,
        String job
    ) {
        if (patient == null) {
            throw new NotFoundException("Patient does not exist!");
        }
        if (birthdate == null || height == null || weight == null) {
            throw new IllegalArgumentException("Birthdate, height, and weight cannot be null!");
        }

        patientDetailDao.updatePatient(
            patient,
            phoneNumber,
            picture,
            mailLanguage,
            birthdate,
            bloodType,
            height,
            weight,
            smokes,
            drinks,
            meds,
            conditions,
            allergies,
            diet,
            hobbies,
            job
        );        
        LOGGER.info("Updated patient with id: {}", patient.getId());
    }
    
}