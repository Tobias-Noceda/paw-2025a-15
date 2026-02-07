package ar.edu.itba.paw.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        final String EMAIL = "created-patient-test@example.com";
        PATIENT.setId(PATIENT_ID);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        Patient patient = patientDao.createPatient(
            EMAIL,
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
        Assert.assertEquals(EMAIL, patientPersisted.getEmail());
        Assert.assertEquals(PATIENT.getPassword(), patientPersisted.getPassword());
        Assert.assertEquals(PATIENT.getName(), patientPersisted.getName());
        Assert.assertEquals(PATIENT.getTelephone(), patientPersisted.getTelephone());
        Assert.assertEquals(PATIENT.getPicture().getId(), patientPersisted.getPicture().getId());
        Assert.assertEquals(PATIENT.getLocale(), patientPersisted.getLocale());
        Assert.assertEquals(PATIENT.getBirthdate(), patientPersisted.getBirthdate());
        Assert.assertEquals(PATIENT.getHeight(), patientPersisted.getHeight());
        Assert.assertEquals(PATIENT.getWeight(), patientPersisted.getWeight());
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

        final String TELEPHONE = "11 1234 5678";
        //pic
        //locale
        //birthdate
        final BloodTypeEnum BLOOD_TYPE = TestData.PatientDetails.BLOOD_TYPE_1;
        final BigDecimal HEIGHT = TestData.PatientDetails.HEIGHT;
        final BigDecimal WEIGHT = TestData.PatientDetails.WEIGHT;
        final Boolean SMOKES = TestData.PatientDetails.SMOKES_1;
        final Boolean DRINKS = TestData.PatientDetails.DRINKS_1;
        final String MEDS = TestData.PatientDetails.MEDS;
        final String CONDITIONS = TestData.PatientDetails.CONDITIONS;
        final String ALLERGIES = TestData.PatientDetails.ALLERGIES;
        final String DIET = TestData.PatientDetails.DIET;
        final String HOBBIES = TestData.PatientDetails.HOBBIES;
        final String JOB = TestData.PatientDetails.JOB_1;
        //insurance
        //insuranceNumber

        patientDao.updatePatient(PATIENT, TELEPHONE, PATIENT.getPicture(), PATIENT.getLocale(), PATIENT.getBirthdate(), BLOOD_TYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB, null, null);

        Assert.assertEquals(PATIENT.getTelephone(), TELEPHONE);
        //
        //
        //
        Assert.assertEquals(PATIENT.getBloodType(), BLOOD_TYPE);
        Assert.assertEquals(PATIENT.getHeight(), HEIGHT);
        Assert.assertEquals(PATIENT.getWeight(), WEIGHT);
        Assert.assertEquals(PATIENT.getSmokes(), SMOKES);
        Assert.assertEquals(PATIENT.getDrinks(), DRINKS);
        Assert.assertEquals(PATIENT.getMeds(), MEDS);
        Assert.assertEquals(PATIENT.getConditions(), CONDITIONS);
        Assert.assertEquals(PATIENT.getAllergies(), ALLERGIES);
        Assert.assertEquals(PATIENT.getDiet(), DIET);
        Assert.assertEquals(PATIENT.getHobbies(), HOBBIES);
        Assert.assertEquals(PATIENT.getJob(), JOB);
        //
        //
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testGetPatientById() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        Optional<Patient> foundPatient = patientDao.getPatientById(PATIENT_ID);

        Assert.assertNotNull(foundPatient);
        Assert.assertTrue(foundPatient.isPresent());
        Assert.assertEquals(PATIENT, foundPatient.get());
        Assert.assertEquals(PATIENT.getEmail(), foundPatient.get().getEmail());
        Assert.assertEquals(PATIENT.getPassword(), foundPatient.get().getPassword());
        Assert.assertEquals(PATIENT.getName(), foundPatient.get().getName());
        Assert.assertEquals(PATIENT.getTelephone(), foundPatient.get().getTelephone());
        Assert.assertEquals(PATIENT.getPicture().getId(), foundPatient.get().getPicture().getId());
        Assert.assertEquals(PATIENT.getLocale(), foundPatient.get().getLocale());
        Assert.assertEquals(PATIENT.getBirthdate(), foundPatient.get().getBirthdate());
        Assert.assertEquals(PATIENT.getHeight(), foundPatient.get().getHeight());
        Assert.assertEquals(
            PATIENT.getWeight().setScale(2, RoundingMode.HALF_UP),
            foundPatient.get().getWeight().setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testGetPatientByIdNonexistentPatient() {
        final long PATIENT_ID = 0L;

        Optional<Patient> foundPatient = patientDao.getPatientById(PATIENT_ID);

        Assert.assertNotNull(foundPatient);
        Assert.assertFalse(foundPatient.isPresent());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testGetAuthDoctorsByPatientIdAndNameCount() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        int count = patientDao.getAuthDoctorsByPatientIdAndNameCount(PATIENT_ID, DOC_NAME);

        Assert.assertNotEquals(0, count);
        Assert.assertEquals(100, count);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testGetAuthDoctorsByPatientIdAndNameCountNullName() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        int count = patientDao.getAuthDoctorsByPatientIdAndNameCount(PATIENT_ID, null);

        Assert.assertNotEquals(0, count);
        Assert.assertEquals(100, count);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testGetAuthDoctorsByPatientIdAndNameCountNonexistentPatient() {
        final long PATIENT_ID = 0L;

        int count = patientDao.getAuthDoctorsByPatientIdAndNameCount(PATIENT_ID, null);

        Assert.assertEquals(0, count);
    }

}