package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.DoctorDao;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.entities.DoctorVacationId;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class DoctorServiceImpl implements DoctorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorServiceImpl.class);

    @Autowired
    private DoctorDao doctorDao;

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

    @Transactional
    @Override
    public void deleteDoctor(long doctorId) {
        doctorDao.deleteDoctor(doctorId);
        LOGGER.info("Deleted doctor with id: {}", doctorId);
    }

    @Transactional
    @Override
    public Optional<Doctor> getDoctorById(long id) {
        Optional<Doctor> doctor = doctorDao.getDoctorById(id);
        doctor.ifPresent(this::initializeDoctorRelations);
        return doctor;
    }

    @Override
    public boolean licenceExists(String licence) {
        return doctorDao.licenceExists(licence);
    }

    @Transactional
    @Override
    public Doctor createDoctor(String email, String password, String name, String telephone, String doctorLicense, SpecialtyEnum specialty, List<Long> insurances, LocaleEnum locale, String token) {
        if(us.getUserByEmail(email).isPresent()) throw new AlreadyExistsException("User with email: " + email + " already exists!");
        File picture = fs.findById(1).orElseThrow(() -> new NotFoundException("Default picture not found!"));
        List<Insurance> insuranceEntities;
        if (insurances == null || insurances.isEmpty()) {
            insuranceEntities = Collections.emptyList();
        } else {
            insuranceEntities = new ArrayList<>();
            for (long insuranceId : insurances) {
                Insurance insurance = is.getInsuranceById(insuranceId)
                        .orElseThrow(() -> new NotFoundException("Insurance with id: " + insuranceId + " does not exist!"));
                insuranceEntities.add(insurance);
            }
        }
        
        Doctor newDoctor = doctorDao.createDoctor(email, passwordEncoder.encode(password), name, telephone, picture.getId(), locale, doctorLicense, specialty, insuranceEntities);
        
        es.sendWelcomeAndVerifyEmail(newDoctor, token);
        
        return newDoctor;
    }

    @Transactional
    @Override
        public void updateDoctor(
        Doctor doctor,
        String phoneNumber,
        File picture,
        LocaleEnum mailLanguage,
        final List<Long> insurancesIds
    ) {
        if (doctor == null || getDoctorById(doctor.getId()).isEmpty()) {
            throw new NotFoundException("Doctor not found!");
        }
        
        List<Insurance> insurances = new ArrayList<>();
        for (long insurance : insurancesIds) {
            Insurance insuranceEntity = is.getInsuranceById(insurance)
                    .orElseThrow(() -> new NotFoundException("Insurance with id: " + insurance + " does not exist!"));
            insurances.add(insuranceEntity);
        }
        doctorDao.updateDoctor(doctor.getId(), phoneNumber, picture.getId(), mailLanguage, insurances);
        LOGGER.info("Updated doctor with id: {}", doctor.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Long insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        if (insuranceId != null) {
            is.getInsuranceById(insuranceId).orElseThrow(() -> new NotFoundException("Insurance with id: " + insuranceId + " does not exist!"));
        }

        LOGGER.info("Fetching doctors page: {} with page size: {}", page, pageSize);
        List<Doctor> doctors = doctorDao.getDoctorsPageByParams(name, specialty, insuranceId, weekday, orderBy, page, pageSize);
        doctors.forEach(this::initializeDoctorRelations);
        return doctors;
    }

    @Transactional(readOnly = true)
    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Long insuranceId, WeekdayEnum weekday) {
        if (insuranceId != null) {
            is.getInsuranceById(insuranceId).orElseThrow(() -> new NotFoundException("Insurance with id: " + insuranceId + " does not exist!"));
        }
        return doctorDao.getTotalDoctorsByParams(name, specialty, insuranceId, weekday);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Patient> getAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        doctorDao.getDoctorById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorDao.searchAuthPatientsPageByDoctorAndName(doctorId, name, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public int getAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        doctorDao.getDoctorById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorDao.searchAuthPatientsCountByDoctorAndName(doctorId, name);
    }

    @Transactional
    @Override
    public DoctorVacation createDoctorVacation(long doctorId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }
        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date and end date cannot be in the past.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        Doctor doctor = doctorDao.getDoctorById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        
        return doctorDao.createDoctorVacation(doctor.getId(), startDate, endDate);
    }

    @Transactional
    @Override
    public void deleteDoctorVacation(long doctorId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }
        Doctor doctor = doctorDao.getDoctorById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        doctorDao.deleteDoctorVacation(new DoctorVacationId(doctor.getId(), startDate, endDate));
        LOGGER.info("Deleted vacation for doctor with id: {}", doctorId);
    }

    @Override
    public boolean vacationExists(long doctorId, LocalDate startDate, LocalDate endDate) {
        return doctorDao.vacationExists(doctorId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DoctorVacation> getDoctorVacationsPastPage(long doctorId, int page, int pageSize) {
        doctorDao.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorDao.getDoctorVacationsPastPage(doctorId, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public int getDoctorVacationsPastCount(long doctorId) {
        doctorDao.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorDao.getDoctorVacationsPastCount(doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DoctorVacation> getDoctorVacationsFuturePage(long doctorId, int page, int pageSize) {
        doctorDao.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorDao.getDoctorVacationsFuturePage(doctorId, page, pageSize);
    }

    private void initializeDoctorRelations(Doctor doctor) {
        if (doctor == null) {
            return;
        }
        if (doctor.getPicture() != null) {
            doctor.getPicture().getId();
        }
        if (doctor.getInsurances() != null) {
            doctor.getInsurances().size();
        }
        if (doctor.getSingleShifts() != null) {
            doctor.getSingleShifts().size();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public int getDoctorVacationsFutureCount(long doctorId) {
        doctorDao.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorDao.getDoctorVacationsFutureCount(doctorId);
    }
}
