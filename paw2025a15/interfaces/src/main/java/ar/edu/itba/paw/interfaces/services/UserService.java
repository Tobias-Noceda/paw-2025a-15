package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.User;

public interface UserService {
    public User create(String email, String password, String name, long pictureId);

    User createDoctorUser(String email, String password, String name, long pictureID, String licence, String speciality);

    public User create(String email, String password, String name);

    public Optional<User> getUserById(long id);
}
