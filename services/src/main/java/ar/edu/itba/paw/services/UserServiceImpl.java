package ar.edu.itba.paw.services;

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
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService es;

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
}
