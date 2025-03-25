package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.User;

public interface UserService {
    User create(final String email, final String password);

    Optional<User> findById(long id);
}