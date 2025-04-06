package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.User;

public interface UserService {
    public User create(String email, String password, String name, long pictureId);

    public User create(String email, String password, String name);

    public User createDoctor(String email, String password, String name, String licence, SpecialtyEnum speciality);

    public User createDoctor(String email, String password, String name, long pictureID, String licence, SpecialtyEnum speciality);

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);
}
