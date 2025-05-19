package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

public interface UserService {

    public User create(String email, String password, String name, String telephone, UserRoleEnum role, LocaleEnum locale);

    public void editUser(long id, String name, String telephone, File picture);

    public void updateLocale(long userId, LocaleEnum locale);

    public Optional<User> getUserById(long id);

    public Optional<File> getUserPicture(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> getAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize);

    public int getAuthPatientsCountByDoctorIdAndName(long doctorId, String name);

    public void changePasswordByID(long id, String password);

    public void askPasswordRecover(String email);
}