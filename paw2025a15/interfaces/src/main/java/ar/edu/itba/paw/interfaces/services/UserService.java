package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

public interface UserService {
    public User createPatient(String email, String password, String name, String telephone, UserRoleEnum role, LocaleEnum locale);

    public User createDoctor(String email, String password, String name, String telephone, String licence, SpecialtyEnum speciality, LocaleEnum locale);

    public void editUser(long id, String name, String telephone, long pictureId);

    public void updateLocale(long userId, LocaleEnum locale);

    public Optional<User> getUserById(long id);

    public Optional<File> getUserPicture(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> getAuthPatientsPageByDoctorId(long id, int page, int pageSize);

    public int getAuthPatientsCountByDoctorId(long id);

    public List<User> searchAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize);

    public int searchAuthPatientsCountByDoctorIdAndName(long doctorId, String name);

    public void changePasswordByID(long id, String password);

    public User getCurrentUser();
}