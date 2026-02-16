package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService es;

    @Autowired
    private PatientService ps;

    @Autowired
    private DoctorService ds;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserById(long id) {
        return userDao.getUserById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
    
    @Transactional
    @Override
    public void changePasswordByID(long id, String password) {
        User user = getUserById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " does not exist!"));
        user.setPassword(passwordEncoder.encode(password));
        LOGGER.info("Changed password for user with id: {}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public void askPasswordRecover(String email) {
        User user = getUserByEmail(email).orElseThrow(() -> new NotFoundException("User with email: " + email + " does not exist!"));
        es.sendPasswordResetEmail(user, UUID.randomUUID().toString());
        LOGGER.info("Password recovery requested for user with email: {}", email);
    }

    @Transactional(readOnly = true)
    @Override
    public void verifyUser(String email) {
        getUserByEmail(email).orElseThrow(() -> new NotFoundException("User with email: " + email + " does not exist!"));
        userDao.verifyUser(email);
        LOGGER.info("Verified user with email: {}", email);
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Argentina/Buenos_Aires")
    public void deleteUnverifiedUsers() {
        List<User> unverifiedUsers = userDao.getUnverifiedUsersOlderThanDays(2);
        
        for (User user : unverifiedUsers) {
            if (user.getRole().equals(UserRoleEnum.PATIENT)) {
                ps.deletePatientById(user.getId());
            } else if (user.getRole().equals(UserRoleEnum.DOCTOR)) {
                ds.deleteDoctor(user.getId());
            }
            LOGGER.info("Deleted unverified user with email: {}", user.getEmail());
        }
        LOGGER.info("Tomorrow appointments reminder sent. At " + LocalDateTime.now().toLocalTime());
    }
}
