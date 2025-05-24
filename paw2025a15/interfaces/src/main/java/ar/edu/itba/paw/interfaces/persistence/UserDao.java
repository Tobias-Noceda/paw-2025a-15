package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

public interface UserDao {
    public User create(String email, String password, String name, String telephone, UserRoleEnum role, File picture, LocaleEnum locale);

    public void editUser(long id, String name, String telephone, File picture);

    public void updateLocale(long userId, LocaleEnum locale);

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> searchAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize);

    public int searchAuthPatientsCountByDoctorIdAndName(long doctorId, String name);

    void changePasswordByID(long id, String password);
}