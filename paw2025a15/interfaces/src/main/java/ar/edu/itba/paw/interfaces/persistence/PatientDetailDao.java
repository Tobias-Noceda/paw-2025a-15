package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.PatientDetail;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;

public interface PatientDetailDao {
    public PatientDetail create(User patient, LocalDate birthdate, BloodTypeEnum bloodType, Double height, Double weight, Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job);

    public void updatePatientDetails(long patientId, LocalDate birthdate, BloodTypeEnum bloodType, Double height, Double weight, Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job);

    public Optional<PatientDetail> getDetailByPatientId(long patientId);
}
