package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

import ar.edu.itba.paw.models.entities.User;

public interface UserDao {
    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);

    void changePasswordByID(long id, String password);
}