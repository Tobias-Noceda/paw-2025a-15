package ar.edu.itba.paw.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.PatientDao;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;

@Repository
public class PatientJpaDao implements PatientDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Patient createPatient(
        String email,
        String password,
        String name,
        String telephone,
        File picture,
        LocaleEnum locale,
        LocalDate birthDate,
        BigDecimal height,
        BigDecimal weight
    ) {
        Patient patient = new Patient(email, password, name, telephone, picture, LocalDate.now(), locale, birthDate, height, weight);
        em.persist(patient);
        return patient;
    }

    @Override
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
    ) {
        if (phoneNumber != null) patient.setTelephone(phoneNumber);
        if (picture != null) patient.setPicture(picture);
        if (mailLanguage != null) patient.setLocale(mailLanguage);
        if (birthdate != null) patient.setBirthdate(birthdate);
        if (bloodType != null) patient.setBloodType(bloodType);
        if (height != null) patient.setHeight(height);
        if (weight != null) patient.setWeight(weight);
        if (smokes != null) patient.setSmokes(smokes);
        if (drinks != null) patient.setDrinks(drinks);
        if (meds != null) patient.setMeds(meds);
        if (conditions != null) patient.setConditions(conditions);
        if (allergies != null) patient.setAllergies(allergies);
        if (diet != null) patient.setDiet(diet);
        if (hobbies != null) patient.setHobbies(hobbies);
        if (job != null) patient.setJob(job);
        if (insurance != null) patient.setInsurance(insurance);
        if (insuranceNumber != null) patient.setInsuranceNumber(insuranceNumber);
        em.merge(patient);
    }

    @Override
    public Optional<Patient> getPatientById(long patientId) {
        Patient patient = em.find(Patient.class, patientId);
        return patient != null ? Optional.of(patient) : Optional.empty();
    }
}