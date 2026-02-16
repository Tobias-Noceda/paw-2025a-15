package ar.edu.itba.paw.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.PatientDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class PatientServiceImpl implements PatientService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientServiceImpl.class);

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private UserService us;

    @Autowired
    private FileService fs;

    @Autowired
    private InsuranceService is;

    @Autowired
    private EmailService es;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly=true)
    @Override
    public void deletePatientById(long patientId) {
        getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        patientDao.deletePatientById(patientId);
        LOGGER.info("Deleted patient with id: {}", patientId);
    }

    @Transactional(readOnly=true)
    @Override
    public Optional<Patient> getPatientById(long id) {
        return patientDao.getPatientById(id);
    }

    @Transactional
    @Override
    public Patient createPatient(String email, String password, String name, String telephone, LocaleEnum locale, LocalDate birthDate, BigDecimal height, BigDecimal weight, String token) {
        if(us.getUserByEmail(email).isPresent()) throw new AlreadyExistsException("User with email: " + email + " already exists!");
        File picture = fs.findById(1).orElseThrow(() -> new NotFoundException("Default picture not found!"));
        Patient newPatient = patientDao.createPatient(email, passwordEncoder.encode(password), name, telephone, picture, locale, birthDate, height, weight);
        es.sendWelcomeAndVerifyEmail(newPatient, token);
        return newPatient;
    }

    @Transactional
    @Override
    public void updatePatient(
        Patient patient,
        String phoneNumber,
        Long pictureId,
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
        String job,
        Long insuranceId,
        String insuranceNumber
    ) {
        if (patient == null || getPatientById(patient.getId()).isEmpty()) {
            throw new NotFoundException("Patient does not exist!");
        }
        Insurance insurance = null;
        String realInsuranceNumber = null;
        if (insuranceId != null) {
            insurance = is.getInsuranceById(insuranceId)
                .orElseThrow(() -> new NotFoundException("Insurance with id: " + insuranceId + " does not exist!"));
            realInsuranceNumber = insuranceNumber;
        }
        File picture = null;
        if (pictureId != null) {
            picture = fs.findById(pictureId)
                .orElseThrow(() -> new NotFoundException("File with id: " + pictureId + " does not exist!"));
        }

        

        patientDao.updatePatient(
            patient.getId(),
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
            job,
            insurance,
            realInsuranceNumber
        );        
        LOGGER.info("Updated patient with id: {}", patient.getId());
    }

    @Transactional(readOnly=true)
    @Override
    public int getAuthDoctorsByPatientIdAndNameCount(long patientId, String name) {
        getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return patientDao.getAuthDoctorsByPatientIdAndNameCount(patientId, name);
    }

    @Transactional(readOnly=true)
    @Override
    public List<Doctor> getAuthDoctorsByPatientIdAndNamePage(long patientId, String name, int page, int pageSize) {
        getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return patientDao.getAuthDoctorsByPatientIdAndNamePage(patientId, name, page, pageSize);
    }
    
}