package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.PatientDetail;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;

public interface PatientDetailService {
    public User createPatient(String email, String password, String name, String telephone, LocaleEnum locale);

    public void updatePatientDetails(long patientId, LocalDate birthdate, BloodTypeEnum bloodType, Double height, Double weight, Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job);

    public Optional<PatientDetail> getDetailByPatientId(long patientId);
}
