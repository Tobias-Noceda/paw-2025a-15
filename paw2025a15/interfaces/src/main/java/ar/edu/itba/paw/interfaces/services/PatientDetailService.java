package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.BloodTypeEnum;
import ar.edu.itba.paw.models.PatientDetail;

public interface PatientDetailService {
    public PatientDetail create(long patientId, Integer age, BloodTypeEnum bloodType, Double height, Double weight, Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job);

    public Optional<PatientDetail> getDetailByPatientId(long patientId);    
}
