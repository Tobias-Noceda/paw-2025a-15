package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.entities.User;

public interface UserService {

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);

    public void changePasswordByID(long id, String password);

    public void askPasswordRecover(String email);
}