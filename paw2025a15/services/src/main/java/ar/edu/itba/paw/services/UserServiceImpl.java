package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    @Override//TODO check porfa
    public User createPatient(String email, String password, String name, String telephone, UserRoleEnum role, LocaleEnum locale) {
        User user = userDao.create(email, password, name, telephone, role, 1, locale); // PictureId por defecto
        pds.create(user.getId(), null, null, null, null, null, null, null, null, null, null, null, null);
        return user;
    }

    @Override
    public User createDoctor(String email, String password, String name, String telephone, String licence, SpecialtyEnum speciality, LocaleEnum locale) {
        User doc = userDao.create(email, password, name, telephone, UserRoleEnum.DOCTOR, 1, locale);
        dds.create(doc.getId(), licence, speciality);
        return doc;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userDao.getUserById(id);
    }

    @Override
    public Optional<File> getUserPicture(long id) {
        User user = userDao.getUserById(id).orElse(null);
        if (user == null) return Optional.empty();

        return fs.findById(user.getPictureId());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
    
    @Override
    public List<User> getAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        return userDao.searchAuthPatientsPageByDoctorIdAndName(doctorId, name, page, pageSize);
    }

    @Override
    public int getAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        return userDao.searchAuthPatientsCountByDoctorIdAndName(doctorId, name);
    }

    @Override
    public void changePasswordByID(long id, String password) {
        userDao.changePasswordByID(id, password);
    }

    @Override
    public void editUser(long id, String name, String telephone, long pictureId) {
        if(getUserById(id).isPresent() && fs.findById(pictureId).isPresent()) userDao.editUser(id, name, telephone, pictureId);
    }

    @Override
    public User getCurrentUser() {
        Authentication session = SecurityContextHolder.getContext().getAuthentication();

        if (session != null) {
            return userDao.getUserByEmail(session.getName()).orElse(null);
        }

        return null;
    }

    @Override
    public void updateLocale(long userId, LocaleEnum locale) {
        userDao.updateLocale(userId, locale);
    }
}
