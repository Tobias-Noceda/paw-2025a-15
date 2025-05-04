package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private FileService fs;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override//TODO check porfa
    public User createPatient(String email, String password, String name, String telephone, UserRoleEnum role, LocaleEnum locale) {
        if(getUserByEmail(email).isPresent()) return null;
        User user = userDao.create(email, passwordEncoder.encode(password), name, telephone, role, 1, locale); // PictureId por defecto
        pds.create(user.getId(), null, null, null, null, null, null, null, null, null, null, null, null);
        return user;
    }

    @Transactional
    @Override
    public User createDoctor(String email, String password, String name, String telephone, String licence, SpecialtyEnum speciality, LocaleEnum locale) {
        if(getUserByEmail(email).isPresent()) return null;
        User doc = userDao.create(email, passwordEncoder.encode(password), name, telephone, UserRoleEnum.DOCTOR, 1, locale); // PictureId por defecto
        dds.create(doc.getId(), licence, speciality);
        return doc;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserById(long id) {
        return userDao.getUserById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<File> getUserPicture(long id) {
        User user = getUserById(id).orElse(null);
        if (user == null) return Optional.empty();
        return fs.findById(user.getPictureId());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAuthPatientsPageByDoctorId(long id, int page, int pageSize) {
        return userDao.getAuthPatientsPageByDoctorId(id, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public int getAuthPatientsCountByDoctorId(long id) {
        return userDao.getAuthPatientsCountByDoctorId(id);
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<User> searchAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        return userDao.searchAuthPatientsPageByDoctorIdAndName(doctorId, name, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public int searchAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        return userDao.searchAuthPatientsCountByDoctorIdAndName(doctorId, name);
    }

    @Transactional
    @Override
    public void changePasswordByID(long id, String password) {
        if(getUserById(id).isPresent()) userDao.changePasswordByID(id, passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public void editUser(long id, String name, String telephone, long pictureId) {
        User user = getUserById(id).orElse(null);
        if(user == null) return;
        if(fs.findById(pictureId).isPresent()) userDao.editUser(id, name, telephone, pictureId);
    }

    @Override
    public User getCurrentUser() {//TODO: capaz va directo en advice, por lo que no testee
        Authentication session = SecurityContextHolder.getContext().getAuthentication();
        if (session != null) return userDao.getUserByEmail(session.getName()).orElse(null);
        return null;
    }

    @Transactional
    @Override
    public void updateLocale(long userId, LocaleEnum locale) {
        userDao.updateLocale(userId, locale);
    }
}
