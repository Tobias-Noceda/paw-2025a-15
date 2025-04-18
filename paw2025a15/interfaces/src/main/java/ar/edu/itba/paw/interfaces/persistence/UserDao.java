package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.User;

public interface UserDao {
    public User create(String email, String password, String name, long pictureId);

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> getAuthPatientsByDoctorId(long id);
}