package ar.edu.itba.paw.interfaces.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;

public interface UserService {

    public Doctor createDoctor(String email, String password, String name, String telephone, File picture, LocaleEnum locale, String licence, SpecialtyEnum specialty);
    
    public Patient createPatient(String email, String password, String name, String telephone, File picture, LocaleEnum locale, LocalDate birthDate, BigDecimal height, BigDecimal weight);

    public void editUser(long id, String name, String telephone, File picture);

    public void updateLocale(long userId, LocaleEnum locale);

    public Optional<User> getUserById(long id);

    public Optional<Patient> getPatientById(long id);

    public Optional<Doctor> getDoctorById(long id);

    public Optional<File> getUserPicture(long id);

    public Optional<User> getUserByEmail(String email);

    public List<User> getAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize);

    public int getAuthPatientsCountByDoctorIdAndName(long doctorId, String name);

    public void changePasswordByID(long id, String password);

    public void askPasswordRecover(String email);
}