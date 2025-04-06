package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

import ar.edu.itba.paw.models.User;

public interface UserDao {
    public User create(String email, String password, String name, long pictureId);

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);
}