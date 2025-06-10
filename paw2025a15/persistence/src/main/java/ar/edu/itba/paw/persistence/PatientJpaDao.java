package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PatientDao;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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
        String job
    ) {
        patient.setTelephone(phoneNumber);
        if (picture != null) {
            patient.setPicture(picture);
        }
        patient.setLocale(mailLanguage);
        patient.setBirthdate(birthdate);
        patient.setBloodType(bloodType);
        patient.setHeight(height);
        patient.setWeight(weight);
        patient.setSmokes(smokes);
        patient.setDrinks(drinks);
        patient.setMeds(meds);
        patient.setConditions(conditions);
        patient.setAllergies(allergies);
        patient.setDiet(diet);
        patient.setHobbies(hobbies);
        patient.setJob(job);
        em.merge(patient);
    }

    @Override
    public Optional<Patient> getPatientById(long patientId) {
        Patient patient = em.find(Patient.class, patientId);
        return patient != null ? Optional.of(patient) : Optional.empty();
    }
}