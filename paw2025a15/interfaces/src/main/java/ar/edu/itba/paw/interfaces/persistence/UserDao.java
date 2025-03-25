package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

import ar.edu.itba.paw.models.User;

public interface UserDao {
    User create(final String email, final String password);

    Optional<User> findById(long id);
}