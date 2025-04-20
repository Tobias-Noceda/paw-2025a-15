package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao;

    @Autowired
    private DoctorDetailService dds;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(String email, String password, String name, String telephone, long pictureId) {
        return userDao.create(email, password, name, telephone, UserRoleEnum.PATIENT, pictureId);
    }

    @Override
    public User create(String email, String password, String name, String telephone) {
        return userDao.create(email, password, name, telephone, UserRoleEnum.PATIENT, 1);//TODO armar una funcion en fileService que traiga la picDefault de users//O quiza simplemente verificacion y obligar al registrarse ingresar una foto
    }

    @Override
    public User createDoctor(String email, String password, String name, String telephone, String licence, SpecialtyEnum speciality){
        User doc = userDao.create(email ,password, name, telephone, UserRoleEnum.DOCTOR, 1);
        dds.create(doc.getId(), licence, speciality);
        return doc;
    }

    @Override
    public User createDoctor(String email, String password, String name, String teleophone, long pictureID, String licence, SpecialtyEnum speciality){
        User doc = userDao.create(email ,password, name, teleophone, UserRoleEnum.DOCTOR, pictureID);
        dds.create(doc.getId(), licence, speciality);
        return doc;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userDao.getUserById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public List<User> getAuthPatientsByDoctorId(long id) {
        return userDao.getAuthPatientsByDoctorId(id);
    }

    @Override
    public void changePassword(String email, String password) {
        userDao.changePassword(email, password);
    }
}
