package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
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
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
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
    public User create(String email, String password, String name, String telephone, UserRoleEnum role, LocaleEnum locale){
        if(getUserByEmail(email).isPresent()) throw new AlreadyExistsException("User with email: " + email + " already exists!");
        File defaultPic = fs.findById(1).orElseThrow(()-> new NotFoundException("Default picture not found!"));
        User user = userDao.create(email, passwordEncoder.encode(password), name, telephone, role, defaultPic, locale); // PictureId por defecto
        if(user == null){
            LOGGER.error("Failed to create user for email: {} at {}", email, LocalDateTime.now()); 
            throw new RuntimeException("Failed to create user for email: " + email);
        }
        LOGGER.info("Successfully created user with email: {}", email);
        return user;
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
