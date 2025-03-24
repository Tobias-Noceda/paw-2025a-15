package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;

public interface UserService {
    User createUser(final String email, final String password);
}