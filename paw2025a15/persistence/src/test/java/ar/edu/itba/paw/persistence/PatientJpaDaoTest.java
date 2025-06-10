package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PatientJpaDaoTest {

    @Autowired
    private PatientJpaDao patientDao;

    @PersistenceContext
    private EntityManager em;
    
    @Test
    public void testCreate(){
        final Patient PATIENT = TestData.Users.patient;
        final Long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(PATIENT_ID);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        Patient patient = patientDao.createPatient(
            "created-patient-test@example.com",
            PATIENT.getPassword(),
            PATIENT.getName(),
            PATIENT.getTelephone(),
            PATIENT.getPicture(),
            PATIENT.getLocale(),
            PATIENT.getBirthdate(),
            PATIENT.getHeight(),
            PATIENT.getWeight()
        );
        Patient patientPersisted = em.find(Patient.class, patient.getId());

        Assert.assertNotNull(patientPersisted);
        Assert.assertEquals(PATIENT.getBirthdate(), patientPersisted.getBirthdate());
        Assert.assertEquals(PATIENT.getHeight(), patientPersisted.getHeight());
        Assert.assertEquals(PATIENT.getWeight(), patientPersisted.getWeight());
    }

    @Test
    public void testCreateNotNullBloodType(){
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        final BloodTypeEnum BLOOD_TYPE = TestData.PatientDetails.newPatientDetail.getBloodType();
        final Boolean SMOKES = TestData.PatientDetails.patientDetail.getSmokes();
        final Boolean DRINKS = TestData.PatientDetails.patientDetail.getDrinks();
        final String MEDS = TestData.PatientDetails.patientDetail.getMeds();
        final String CONDITIONS = TestData.PatientDetails.patientDetail.getConditions();
        final String ALLERGIES = TestData.PatientDetails.patientDetail.getAllergies();
        final String DIET = TestData.PatientDetails.patientDetail.getDiet();
        final String HOBBIES = TestData.PatientDetails.patientDetail.getHobbies();
        final String JOB = TestData.PatientDetails.patientDetail.getJob();

        patientDao.updatePatient(PATIENT, PATIENT.getTelephone(), PATIENT.getPicture(), PATIENT.getLocale(), PATIENT.getBirthdate(), BLOOD_TYPE, PATIENT.getHeight(), PATIENT.getWeight(), SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);

        Assert.assertEquals(PATIENT.getBloodType(), BLOOD_TYPE);
        Assert.assertEquals(PATIENT.getSmokes(), SMOKES);
        Assert.assertEquals(PATIENT.getDrinks(), DRINKS);
        Assert.assertEquals(PATIENT.getMeds(), MEDS);
        Assert.assertEquals(PATIENT.getConditions(), CONDITIONS);
        Assert.assertEquals(PATIENT.getAllergies(), ALLERGIES);
        Assert.assertEquals(PATIENT.getDiet(), DIET);
        Assert.assertEquals(PATIENT.getHobbies(), HOBBIES);
        Assert.assertEquals(PATIENT.getJob(), JOB);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testCreateExistentPatient(){
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);
        
        Assert.assertThrows(PersistenceException.class,()->{
            patientDao.createPatient(
                PATIENT.getEmail(),
                PATIENT.getPassword(),
                PATIENT.getName(),
                PATIENT.getTelephone(),
                PATIENT.getPicture(),
                PATIENT.getLocale(),
                PATIENT.getBirthdate(),
                PATIENT.getHeight(),
                PATIENT.getWeight()
            );
            em.flush();
        });
    }
        @Test
    public void testUpdate(){
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        final BloodTypeEnum BLOOD_TYPE = TestData.PatientDetails.patientDetail.getBloodType();
        final Boolean SMOKES = TestData.PatientDetails.patientDetail.getSmokes();
        final Boolean DRINKS = TestData.PatientDetails.patientDetail.getDrinks();
        final String MEDS = TestData.PatientDetails.patientDetail.getMeds();
        final String CONDITIONS = TestData.PatientDetails.patientDetail.getConditions();
        final String ALLERGIES = TestData.PatientDetails.patientDetail.getAllergies();
        final String DIET = TestData.PatientDetails.patientDetail.getDiet();
        final String HOBBIES = TestData.PatientDetails.patientDetail.getHobbies();
        final String JOB = TestData.PatientDetails.patientDetail.getJob();

        patientDao.updatePatient(PATIENT, PATIENT.getTelephone(), PATIENT.getPicture(), PATIENT.getLocale(), PATIENT.getBirthdate(), BLOOD_TYPE, PATIENT.getHeight(), PATIENT.getWeight(), SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);

        Assert.assertEquals(PATIENT.getBloodType(), BLOOD_TYPE);
        Assert.assertEquals(PATIENT.getSmokes(), SMOKES);
        Assert.assertEquals(PATIENT.getDrinks(), DRINKS);
        Assert.assertEquals(PATIENT.getMeds(), MEDS);
        Assert.assertEquals(PATIENT.getConditions(), CONDITIONS);
        Assert.assertEquals(PATIENT.getAllergies(), ALLERGIES);
        Assert.assertEquals(PATIENT.getDiet(), DIET);
        Assert.assertEquals(PATIENT.getHobbies(), HOBBIES);
        Assert.assertEquals(PATIENT.getJob(), JOB);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testPatientById() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        Optional<Patient> foundPatient = patientDao.getPatientById(PATIENT_ID);

        Assert.assertNotNull(foundPatient);
        Assert.assertTrue(foundPatient.isPresent());
        Assert.assertEquals(PATIENT, foundPatient.get());
    }

}