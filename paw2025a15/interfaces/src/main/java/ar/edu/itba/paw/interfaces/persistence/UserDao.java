package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;

public interface UserDao {
    public User create(String email, String password, String name, String telephone, UserRoleEnum role, long pictureId);

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> getAuthPatientsByDoctorId(long id);

    public List<User> searchAuthPatientsByDoctorIdAndName(long doctorId, String name);

    void changePassword(String email, String password);
}