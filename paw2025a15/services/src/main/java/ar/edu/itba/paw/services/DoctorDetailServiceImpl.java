package ar.edu.itba.paw.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.paw.models.enums.DoctorOrderEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;

@Service
public class DoctorDetailServiceImpl implements DoctorDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDetailServiceImpl.class);

    @Autowired
    private DoctorDetailDao doctorDetailDao;

    @Autowired
    private UserService us;

    @Autowired
    private FileService fs;

    @Autowired
    private InsuranceService is;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Optional<Doctor> getDoctorById(long id) {
        return doctorDetailDao.getDoctorById(id);
    }

    @Override
    public boolean licenceExists(String licence) {
        return doctorDetailDao.licenceExists(licence);
    }

    @Transactional
    @Override
    public Doctor createDoctor(String email, String password, String name, String telephone, String doctorLicense, SpecialtyEnum specialty, List<Long> insurances, LocaleEnum locale) {
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
        return doctorDetailDao.createDoctor(email, passwordEncoder.encode(password), name, telephone, picture.getId(), locale, doctorLicense, specialty, insuranceEntities);
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
        doctorDetailDao.updateDoctor(doctor.getId(), phoneNumber, picture.getId(), mailLanguage, insurances);
        LOGGER.info("Updated doctor with id: {}", doctor.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, long insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        is.getInsuranceById(insuranceId).orElseThrow(() -> new NotFoundException("Insurance with id: " + insuranceId + " does not exist!"));
        return doctorDetailDao.getDoctorsPageByParams(name, specialty, insuranceId, weekday, orderBy, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, long insuranceId, WeekdayEnum weekday) {
        is.getInsuranceById(insuranceId).orElseThrow(() -> new NotFoundException("Insurance with id: " + insuranceId + " does not exist!"));
        return doctorDetailDao.getTotalDoctorsByParams(name, specialty, insuranceId, weekday);
    }

    @Transactional
    @Override
    public List<Patient> getAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        doctorDetailDao.getDoctorById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorDetailDao.searchAuthPatientsPageByDoctorAndName(doctorId, name, page, pageSize);
    }

    @Transactional
    @Override
    public int getAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        doctorDetailDao.getDoctorById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorDetailDao.searchAuthPatientsCountByDoctorAndName(doctorId, name);
    }
}