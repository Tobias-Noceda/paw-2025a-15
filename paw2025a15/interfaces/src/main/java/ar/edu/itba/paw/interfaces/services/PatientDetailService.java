package ar.edu.itba.paw.interfaces.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;

public interface PatientDetailService {
    public Patient createPatient(String email, String password, String name, String telephone, LocaleEnum locale, LocalDate birthDate, BigDecimal height, BigDecimal weight);

    public Optional<Patient> getPatientById(long id);

    public void updatePatient(Patient patient, String phoneNumber, File picture, LocaleEnum mailLanguage, LocalDate birthdate, BloodTypeEnum bloodType, BigDecimal height, BigDecimal weight, Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job);
}
