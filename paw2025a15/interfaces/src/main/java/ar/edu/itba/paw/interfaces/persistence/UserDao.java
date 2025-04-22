package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;

public interface UserDao {
    public User create(String email, String password, String name, String telephone, UserRoleEnum role, long pictureId);

    public void editUser(long id, String name, String telephone, long pictureId);

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> getAuthPatientsByDoctorId(long id);

    void changePassword(String email, String password);

    void changePasswordByID(long id, String password);

    void UpdatePhoneNumber(long id, String number);
}