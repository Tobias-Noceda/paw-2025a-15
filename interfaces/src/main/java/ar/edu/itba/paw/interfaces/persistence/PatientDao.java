package ar.edu.itba.paw.interfaces.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;

public interface PatientDao {
    
    public Patient createPatient(String email, String password, String name, String telephone, File picture, LocaleEnum locale, LocalDate birthDate, BigDecimal height, BigDecimal weight);

    public void updatePatient(
        Patient patient,
        String phoneNumber,
        File picture,
        LocaleEnum mailLanguage,
        LocalDate birthdate,
        BloodTypeEnum bloodType,
        BigDecimal height,
        BigDecimal weight,
        Boolean smokes,
        Boolean drinks,
        String meds,
        String conditions,
        String allergies,
        String diet,
        String hobbies,
        String job,
        Insurance insurance,
        String insuranceNumber
    );

    public Optional<Patient> getPatientById(long patientId);

    public int getAuthDoctorsByPatientIdAndNameCount(long patientId, String name);

    public List<Doctor> getAuthDoctorsByPatientIdAndNamePage(long patientId, String name, int page, int pageSize);
}
