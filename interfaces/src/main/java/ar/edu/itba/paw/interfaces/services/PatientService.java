package ar.edu.itba.paw.interfaces.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;

public interface PatientService {
    
    public void deletePatientById(long patientId);

    public Patient createPatient(String email, String password, String name, String telephone, LocaleEnum locale, LocalDate birthDate, BigDecimal height, BigDecimal weight);

    public Optional<Patient> getPatientById(long id);

    public void updatePatient(
        Patient patient,
        String phoneNumber,
        Long pictureId,
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
        Long insuranceId,
        String insuranceNumber
    );

    public int getAuthDoctorsByPatientIdAndNameCount(long patientId, String name);

    public List<Doctor> getAuthDoctorsByPatientIdAndNamePage(long patientId, String name, int page, int pageSize);

}
