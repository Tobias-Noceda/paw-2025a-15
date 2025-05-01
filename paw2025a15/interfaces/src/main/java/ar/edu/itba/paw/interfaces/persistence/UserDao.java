package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.LocaleEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;

public interface UserDao {
    public User create(String email, String password, String name, String telephone, UserRoleEnum role, long pictureId, LocaleEnum locale);

    public void editUser(long id, String name, String telephone, long pictureId);

    public void updateLocale(long userId, LocaleEnum locale);

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> getAuthPatientsPageByDoctorId(long id, int page, int pageSize);

    public int getAuthPatientsCountByDoctorId(long id);

    public List<User> searchAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize);

    public int searchAuthPatientsCountByDoctorIdAndName(long doctorId, String name);

    void changePassword(String email, String password);

    void changePasswordByID(long id, String password);

    void updatePhoneNumber(long id, String number);
}