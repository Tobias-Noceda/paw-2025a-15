package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public class PatientDetailJpaDao implements PatientDetailDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public PatientDetail create(User patient, LocalDate birthdate, BloodTypeEnum bloodType, Double height,
            Double weight, Boolean smokes, Boolean drinks, String meds, String conditions, String allergies,
            String diet, String hobbies, String job) {
        final PatientDetail pd = new PatientDetail(patient, birthdate, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
        em.persist(pd);
        return pd;
    }

    @Override
    public void updatePatientDetails(long patientId, LocalDate birthdate, BloodTypeEnum bloodType, Double height,
            Double weight, Boolean smokes, Boolean drinks, String meds, String conditions, String allergies,
            String diet, String hobbies, String job) {
        PatientDetail pd = em.find(PatientDetail.class, patientId);
        pd.setBirthdate(birthdate);
        pd.setBloodType(bloodType);
        pd.setHeight(height);
        pd.setWeight(weight);
        pd.setSmokes(smokes);
        pd.setDrinks(drinks);
        pd.setMeds(meds);
        pd.setConditions(conditions);
        pd.setAllergies(allergies);
        pd.setDiet(diet);
        pd.setHobbies(hobbies);
        pd.setJob(job);
        em.merge(pd);
    }

    @Override
    public Optional<PatientDetail> getDetailByPatientId(long patientId) {
        return Optional.ofNullable(em.find(PatientDetail.class, patientId));
    }

}