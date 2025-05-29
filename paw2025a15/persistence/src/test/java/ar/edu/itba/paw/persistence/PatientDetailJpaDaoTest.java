package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.entities.PatientDetail;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PatientDetailJpaDaoTest {

    @Autowired
    private PatientDetailJpaDao patientDetailDao;

    @PersistenceContext
    private EntityManager em;
    
    @Test
    public void testCreate(){
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);
        final LocalDate BIRTHDATE = TestData.PatientDetails.patientDetail.getBirthdate();
        final BloodTypeEnum BLOOD_TYPE = TestData.PatientDetails.patientDetail.getBloodType();
        final Double HEIGHT = TestData.PatientDetails.patientDetail.getHeight();
        final Double WEIGHT = TestData.PatientDetails.patientDetail.getWeight();
        final Boolean SMOKES = TestData.PatientDetails.patientDetail.getSmokes();
        final Boolean DRINKS = TestData.PatientDetails.patientDetail.getDrinks();
        final String MEDS = TestData.PatientDetails.patientDetail.getMeds();
        final String CONDITIONS = TestData.PatientDetails.patientDetail.getConditions();
        final String ALLERGIES = TestData.PatientDetails.patientDetail.getAllergies();
        final String DIET = TestData.PatientDetails.patientDetail.getDiet();
        final String HOBBIES = TestData.PatientDetails.patientDetail.getHobbies();
        final String JOB = TestData.PatientDetails.patientDetail.getJob();

        PatientDetail patientDetail = patientDetailDao.create(PATIENT.getId(), BIRTHDATE, BLOOD_TYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);
        PatientDetail patientDetailPersisted = em.find(PatientDetail.class, patientDetail.getPatientId());

        Assert.assertNotNull(patientDetailPersisted);
        Assert.assertEquals(PATIENT, patientDetailPersisted.getPatient());
        Assert.assertEquals(BIRTHDATE, patientDetailPersisted.getBirthdate());
        Assert.assertEquals(BLOOD_TYPE, patientDetailPersisted.getBloodType());
        Assert.assertEquals(HEIGHT, patientDetailPersisted.getHeight());
        Assert.assertEquals(WEIGHT, patientDetailPersisted.getWeight());
        Assert.assertEquals(SMOKES, patientDetailPersisted.getSmokes());
        Assert.assertEquals(DRINKS, patientDetailPersisted.getDrinks());
        Assert.assertEquals(MEDS, patientDetailPersisted.getMeds());
        Assert.assertEquals(CONDITIONS, patientDetailPersisted.getConditions());
        Assert.assertEquals(ALLERGIES, patientDetailPersisted.getAllergies());
        Assert.assertEquals(DIET, patientDetailPersisted.getDiet());
        Assert.assertEquals(HOBBIES, patientDetailPersisted.getHobbies());
        Assert.assertEquals(JOB, patientDetailPersisted.getJob());
    }

    @Test
    public void testCreateNotNullBloodType(){
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);
        final LocalDate BIRTHDATE = TestData.PatientDetails.patientDetail.getBirthdate();
        final BloodTypeEnum BLOOD_TYPE = TestData.PatientDetails.newPatientDetail.getBloodType();
        final Double HEIGHT = TestData.PatientDetails.patientDetail.getHeight();
        final Double WEIGHT = TestData.PatientDetails.patientDetail.getWeight();
        final Boolean SMOKES = TestData.PatientDetails.patientDetail.getSmokes();
        final Boolean DRINKS = TestData.PatientDetails.patientDetail.getDrinks();
        final String MEDS = TestData.PatientDetails.patientDetail.getMeds();
        final String CONDITIONS = TestData.PatientDetails.patientDetail.getConditions();
        final String ALLERGIES = TestData.PatientDetails.patientDetail.getAllergies();
        final String DIET = TestData.PatientDetails.patientDetail.getDiet();
        final String HOBBIES = TestData.PatientDetails.patientDetail.getHobbies();
        final String JOB = TestData.PatientDetails.patientDetail.getJob();

        PatientDetail patientDetail = patientDetailDao.create(PATIENT.getId(), BIRTHDATE, BLOOD_TYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);
        PatientDetail patientDetailPersisted = em.find(PatientDetail.class, patientDetail.getPatientId());

        Assert.assertNotNull(patientDetailPersisted);
        Assert.assertEquals(PATIENT, patientDetailPersisted.getPatient());
        Assert.assertEquals(BIRTHDATE, patientDetailPersisted.getBirthdate());
        Assert.assertEquals(BLOOD_TYPE, patientDetailPersisted.getBloodType());
        Assert.assertEquals(HEIGHT, patientDetailPersisted.getHeight());
        Assert.assertEquals(WEIGHT, patientDetailPersisted.getWeight());
        Assert.assertEquals(SMOKES, patientDetailPersisted.getSmokes());
        Assert.assertEquals(DRINKS, patientDetailPersisted.getDrinks());
        Assert.assertEquals(MEDS, patientDetailPersisted.getMeds());
        Assert.assertEquals(CONDITIONS, patientDetailPersisted.getConditions());
        Assert.assertEquals(ALLERGIES, patientDetailPersisted.getAllergies());
        Assert.assertEquals(DIET, patientDetailPersisted.getDiet());
        Assert.assertEquals(HOBBIES, patientDetailPersisted.getHobbies());
        Assert.assertEquals(JOB, patientDetailPersisted.getJob());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testCreateExistentPatient(){
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);
        final LocalDate BIRTHDATE = TestData.PatientDetails.patientDetail.getBirthdate();
        final BloodTypeEnum BLOOD_TYPE = TestData.PatientDetails.patientDetail.getBloodType();
        final Double HEIGHT = TestData.PatientDetails.patientDetail.getHeight();
        final Double WEIGHT = TestData.PatientDetails.patientDetail.getWeight();
        final Boolean SMOKES = TestData.PatientDetails.patientDetail.getSmokes();
        final Boolean DRINKS = TestData.PatientDetails.patientDetail.getDrinks();
        final String MEDS = TestData.PatientDetails.patientDetail.getMeds();
        final String CONDITIONS = TestData.PatientDetails.patientDetail.getConditions();
        final String ALLERGIES = TestData.PatientDetails.patientDetail.getAllergies();
        final String DIET = TestData.PatientDetails.patientDetail.getDiet();
        final String HOBBIES = TestData.PatientDetails.patientDetail.getHobbies();
        final String JOB = TestData.PatientDetails.patientDetail.getJob();
        
        Assert.assertThrows(PersistenceException.class,()->{
            patientDetailDao.create(PATIENT.getId(), BIRTHDATE, BLOOD_TYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);
            em.flush();
        });
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testGetDetailByPatientId(){
        final PatientDetail PATIENT_DETAILS = TestData.PatientDetails.patientDetail;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT_DETAILS.getPatient().setId(TestData.Users.patientId);
        PATIENT_DETAILS.getPatient().getPicture().setId(TestData.Images.validImageId);

        Optional<PatientDetail> foundPatientDetail = patientDetailDao.getDetailByPatientId(PATIENT_ID);

        Assert.assertNotNull(foundPatientDetail);
        Assert.assertTrue(foundPatientDetail.isPresent());
        Assert.assertEquals(PATIENT_DETAILS, foundPatientDetail.get());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails-allNull.sql"})
    public void testGetDetailByPatientIdNullDetails(){
        final PatientDetail PATIENT_DETAILS = TestData.PatientDetails.newPatientDetailNullValues;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT_DETAILS.getPatient().setId(TestData.Users.patientId);
        PATIENT_DETAILS.getPatient().getPicture().setId(TestData.Images.validImageId);

        Optional<PatientDetail> foundPatientDetail = patientDetailDao.getDetailByPatientId(PATIENT_ID);

        Assert.assertNotNull(foundPatientDetail);
        Assert.assertTrue(foundPatientDetail.isPresent());
        Assert.assertEquals(PATIENT_DETAILS, foundPatientDetail.get());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails-notNull.sql"})
    public void testGetDetailByPatientIdNotNullDetails(){
        final PatientDetail PATIENT_DETAILS = TestData.PatientDetails.newPatientDetailNotNullValues;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT_DETAILS.getPatient().setId(TestData.Users.patientId);
        PATIENT_DETAILS.getPatient().getPicture().setId(TestData.Images.validImageId);

        Optional<PatientDetail> foundPatientDetail = patientDetailDao.getDetailByPatientId(PATIENT_ID);

        Assert.assertNotNull(foundPatientDetail);
        Assert.assertTrue(foundPatientDetail.isPresent());
        Assert.assertEquals(PATIENT_DETAILS, foundPatientDetail.get());
    }

    @Test
    public void testGetDetailByPatientIdNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.doctorId;

        Optional<PatientDetail> foundPatientDetail = patientDetailDao.getDetailByPatientId(PATIENT_ID);

        Assert.assertNotNull(foundPatientDetail);
        Assert.assertFalse(foundPatientDetail.isPresent());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testUpdatePatientDetails(){
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);
        final PatientDetail PATIENT_DETAIL_OLD = TestData.PatientDetails.patientDetail;
        PATIENT_DETAIL_OLD.setPatient(PATIENT);

        final LocalDate NEW_BIRTHDATE = TestData.PatientDetails.newPatientDetail.getBirthdate();
        final BloodTypeEnum NEW_BLOOD_TYPE = TestData.PatientDetails.newPatientDetail.getBloodType();
        final Double NEW_HEIGHT = TestData.PatientDetails.newPatientDetail.getHeight();
        final Double NEW_WEIGHT = TestData.PatientDetails.newPatientDetail.getWeight();
        final Boolean NEW_SMOKES = TestData.PatientDetails.newPatientDetail.getSmokes();
        final Boolean NEW_DRINKS = TestData.PatientDetails.newPatientDetail.getDrinks();
        final String NEW_MEDS = TestData.PatientDetails.newPatientDetail.getMeds();
        final String NEW_CONDITIONS = TestData.PatientDetails.newPatientDetail.getConditions();
        final String NEW_ALLERGIES = TestData.PatientDetails.newPatientDetail.getAllergies();
        final String NEW_DIET = TestData.PatientDetails.newPatientDetail.getDiet();
        final String NEW_HOBBIES = TestData.PatientDetails.newPatientDetail.getHobbies();
        final String NEW_JOB = TestData.PatientDetails.newPatientDetail.getJob();
        final PatientDetail PATIENT_DETAIL_NEW = TestData.PatientDetails.newPatientDetail;
        PATIENT_DETAIL_NEW.setPatient(PATIENT);

        patientDetailDao.updatePatientDetails(PATIENT.getId(), NEW_BIRTHDATE, NEW_BLOOD_TYPE, NEW_HEIGHT, NEW_WEIGHT, NEW_SMOKES, NEW_DRINKS, NEW_MEDS, NEW_CONDITIONS, NEW_ALLERGIES, NEW_DIET, NEW_HOBBIES, NEW_JOB);
        PatientDetail patientDetailFound = em.find(PatientDetail.class, PATIENT.getId());

        Assert.assertNotNull(patientDetailFound);
        Assert.assertEquals(PATIENT_DETAIL_NEW, patientDetailFound);
        Assert.assertNotEquals(PATIENT_DETAIL_OLD, patientDetailFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testUpdatePatientDetailsNullNewBloodType(){
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);
        final PatientDetail PATIENT_DETAIL_OLD = TestData.PatientDetails.patientDetail;
        PATIENT_DETAIL_OLD.setPatient(PATIENT);

        final LocalDate NEW_BIRTHDATE = TestData.PatientDetails.newPatientDetail.getBirthdate();
        final BloodTypeEnum NEW_BLOOD_TYPE = null;
        final Double NEW_HEIGHT = TestData.PatientDetails.newPatientDetail.getHeight();
        final Double NEW_WEIGHT = TestData.PatientDetails.newPatientDetail.getWeight();
        final Boolean NEW_SMOKES = TestData.PatientDetails.newPatientDetail.getSmokes();
        final Boolean NEW_DRINKS = TestData.PatientDetails.newPatientDetail.getDrinks();
        final String NEW_MEDS = TestData.PatientDetails.newPatientDetail.getMeds();
        final String NEW_CONDITIONS = TestData.PatientDetails.newPatientDetail.getConditions();
        final String NEW_ALLERGIES = TestData.PatientDetails.newPatientDetail.getAllergies();
        final String NEW_DIET = TestData.PatientDetails.newPatientDetail.getDiet();
        final String NEW_HOBBIES = TestData.PatientDetails.newPatientDetail.getHobbies();
        final String NEW_JOB = TestData.PatientDetails.newPatientDetail.getJob();
        final PatientDetail PATIENT_DETAIL_NEW = TestData.PatientDetails.newPatientDetail;
        PATIENT_DETAIL_NEW.setPatient(PATIENT);
        PATIENT_DETAIL_NEW.setBloodType(NEW_BLOOD_TYPE);

        patientDetailDao.updatePatientDetails(PATIENT.getId(), NEW_BIRTHDATE, NEW_BLOOD_TYPE, NEW_HEIGHT, NEW_WEIGHT, NEW_SMOKES, NEW_DRINKS, NEW_MEDS, NEW_CONDITIONS, NEW_ALLERGIES, NEW_DIET, NEW_HOBBIES, NEW_JOB);
        PatientDetail patientDetailFound = em.find(PatientDetail.class, PATIENT.getId());

        Assert.assertNotNull(patientDetailFound);
        Assert.assertEquals(PATIENT_DETAIL_NEW, patientDetailFound);
        Assert.assertNotEquals(PATIENT_DETAIL_OLD, patientDetailFound);

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testUpdatePatientDetailsNonexistentPatient(){
        final long PATIENT_ID =  TestData.Users.doctor.getId();

        final LocalDate NEW_BIRTHDATE = TestData.PatientDetails.newPatientDetail.getBirthdate();
        final BloodTypeEnum NEW_BLOOD_TYPE = TestData.PatientDetails.newPatientDetail.getBloodType();
        final Double NEW_HEIGHT = TestData.PatientDetails.newPatientDetail.getHeight();
        final Double NEW_WEIGHT = TestData.PatientDetails.newPatientDetail.getWeight();
        final Boolean NEW_SMOKES = TestData.PatientDetails.newPatientDetail.getSmokes();
        final Boolean NEW_DRINKS = TestData.PatientDetails.newPatientDetail.getDrinks();
        final String NEW_MEDS = TestData.PatientDetails.newPatientDetail.getMeds();
        final String NEW_CONDITIONS = TestData.PatientDetails.newPatientDetail.getConditions();
        final String NEW_ALLERGIES = TestData.PatientDetails.newPatientDetail.getAllergies();
        final String NEW_DIET = TestData.PatientDetails.newPatientDetail.getDiet();
        final String NEW_HOBBIES = TestData.PatientDetails.newPatientDetail.getHobbies();
        final String NEW_JOB = TestData.PatientDetails.newPatientDetail.getJob();
        
        patientDetailDao.updatePatientDetails(PATIENT_ID, NEW_BIRTHDATE, NEW_BLOOD_TYPE, NEW_HEIGHT, NEW_WEIGHT, NEW_SMOKES, NEW_DRINKS, NEW_MEDS, NEW_CONDITIONS, NEW_ALLERGIES, NEW_DIET, NEW_HOBBIES, NEW_JOB);
        PatientDetail patientDetailFound = em.find(PatientDetail.class, PATIENT_ID);

        Assert.assertNull(patientDetailFound);
    }

}
