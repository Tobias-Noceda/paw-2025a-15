package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.User;

public interface UserService {
    public User create(String email, String password, String name, String telephone, long pictureId);

    public User create(String email, String password, String name, String telephone);

    public User createDoctor(String email, String password, String name, String telephone, String licence, SpecialtyEnum speciality);

    public User createDoctor(String email, String password, String name, String telephone, long pictureID, String licence, SpecialtyEnum speciality);

    public Optional<User> getUserById(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> getAuthPatientsByDoctorId(long id);

    void changePassword(String email, String password);
}
