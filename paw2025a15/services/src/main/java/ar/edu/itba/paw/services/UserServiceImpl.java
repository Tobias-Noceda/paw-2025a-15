package ar.edu.itba.paw.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService es;

    @Autowired
    private FileService fs;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Doctor createDoctor(String email, String password, String name, String telephone, File picture, LocaleEnum locale, String licence, SpecialtyEnum specialty) {
        if (userDao.getUserByEmail(email).isPresent()) {
            throw new AlreadyExistsException("User with email: " + email + " already exists!");
        }
        Doctor doctor = userDao.createDoctor(email, passwordEncoder.encode(password), name, telephone, picture, locale, licence, specialty);
        LOGGER.info("Created doctor user with email: {}", email);
        return doctor;
    }

    @Transactional
    @Override
    public Patient createPatient(String email, String password, String name, String telephone, File picture, LocaleEnum locale, LocalDate birthDate, BigDecimal height, BigDecimal weight) {
        if (userDao.getUserByEmail(email).isPresent()) {
            throw new AlreadyExistsException("User with email: " + email + " already exists!");
        }
        Patient patient = userDao.createPatient(email, passwordEncoder.encode(password), name, telephone, picture, locale, birthDate, height, weight);
        LOGGER.info("Created patient user with email: {}", email);
        return patient;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserById(long id) {
        return userDao.getUserById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<File> getUserPicture(long id) {
        User user = getUserById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " does not exist!"));

        return fs.findById(user.getPicture().getId());//TODO:changed when migrating jpa, check later
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<User> getAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        return userDao.searchAuthPatientsPageByDoctorIdAndName(doctorId, name, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public int getAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        return userDao.searchAuthPatientsCountByDoctorIdAndName(doctorId, name);
    }

    @Transactional
    @Override
    public void changePasswordByID(long id, String password) {
        if(getUserById(id).isEmpty()) throw new NotFoundException("User with id: " + id + " does not exist!");
        userDao.changePasswordByID(id, passwordEncoder.encode(password));
        LOGGER.info("Changed password for user with id: {}", id);
    }

    @Override
    public void askPasswordRecover(String email) {
        User user = getUserByEmail(email).orElseThrow(() -> new NotFoundException("User with email: " + email + " does not exist!"));
        es.sendPasswordResetEmail(user, UUID.randomUUID().toString());
        LOGGER.info("Password recovery requested for user with email: {}", email);
    }

    @Transactional
    @Override
    public void editUser(long id, String name, String telephone, File picture) {
        if(getUserById(id).isEmpty()) throw new NotFoundException("User with id: " + id + " does not exist!");
        if(fs.findById(picture.getId()).isEmpty()) throw new NotFoundException("Picture with id: " + picture.getId() + " does not exist!");
        userDao.editUser(id, name, telephone, picture);
        LOGGER.info("Edited user information for user with id: {}", id);
    }

    @Transactional
    @Override
    public void updateLocale(long userId, LocaleEnum locale) {
        if(getUserById(userId).isEmpty()) throw new NotFoundException("User with id: " + userId + " does not exist!");
        userDao.updateLocale(userId, locale);
        LOGGER.info("Updating locale information for user with id: {}", userId);
    }
}
