package ar.edu.itba.paw.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
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

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
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
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:patientDetails.sql"})
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
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:patientDetails-notNull.sql"})
    public void testUpdate(){
        final Long PATIENT_ID = TestData.Users.patientId;
        // final Patient PATIENT = TestData.Users.patient;
        // PATIENT.setId(PATIENT_ID);
        // PATIENT.getPicture().setId(TestData.Images.validImageId);

        final String NEW_TELEPHONE = TestData.Users.newPatient.getTelephone();
        final File NEW_PIC = TestData.Images.validImage2;
        final Long NEW_PIC_ID = TestData.Images.validImage2Id;
        NEW_PIC.setId(NEW_PIC_ID);
        final LocaleEnum NEW_LOCALE = TestData.Users.newPatient.getLocale();
        final LocalDate NEW_BIRTHDATE = TestData.Users.newPatient.getBirthdate();
        final BloodTypeEnum NEW_BLOODTYPE = TestData.PatientDetails.BLOOD_TYPE_2;
        final BigDecimal NEW_HEIGHT = TestData.PatientDetails.HEIGHT_2;
        final BigDecimal NEW_WEIGHT = TestData.PatientDetails.WEIGHT_2;
        final Boolean NEW_SMOKES = TestData.PatientDetails.SMOKES_2;
        final Boolean NEW_DRINKS = TestData.PatientDetails.DRINKS_2;
        final String NEW_MEDS = TestData.PatientDetails.MEDS_2;
        final String NEW_CONDITIONS = TestData.PatientDetails.CONDITIONS_2;
        final String NEW_ALLERGIES = TestData.PatientDetails.ALLERGIES_2;
        final String NEW_DIET = TestData.PatientDetails.DIET_2;
        final String NEW_HOBBIES = TestData.PatientDetails.HOBBIES_2;
        final String NEW_JOB = TestData.PatientDetails.JOB_2;
        final Insurance NEW_INSURANCE = TestData.Insurances.validInsurance;
        final Long NEW_INSURANCE_ID = TestData.Insurances.validInsuranceId;
        NEW_INSURANCE.setId(NEW_INSURANCE_ID);
        final String NEW_INSURANCENUM = "12345678";

        patientDao.updatePatient(PATIENT_ID, NEW_TELEPHONE, NEW_PIC, NEW_LOCALE, NEW_BIRTHDATE, NEW_BLOODTYPE, NEW_HEIGHT, NEW_WEIGHT, NEW_SMOKES, NEW_DRINKS, NEW_MEDS, NEW_CONDITIONS, NEW_ALLERGIES, NEW_DIET, NEW_HOBBIES, NEW_JOB, NEW_INSURANCE, NEW_INSURANCENUM);
        Patient persistedPatient = em.find(Patient.class, PATIENT_ID);

        Assert.assertEquals(NEW_TELEPHONE, persistedPatient.getTelephone());
        Assert.assertEquals(NEW_PIC_ID, persistedPatient.getPicture().getId());
        Assert.assertEquals(NEW_LOCALE, persistedPatient.getLocale());
        Assert.assertEquals(NEW_BIRTHDATE, persistedPatient.getBirthdate());
        Assert.assertEquals(NEW_BLOODTYPE, persistedPatient.getBloodType());
        Assert.assertEquals(NEW_HEIGHT, persistedPatient.getHeight());
        Assert.assertEquals(NEW_WEIGHT, persistedPatient.getWeight());
        Assert.assertEquals(NEW_SMOKES, persistedPatient.getSmokes());
        Assert.assertEquals(NEW_DRINKS, persistedPatient.getDrinks());
        Assert.assertEquals(NEW_MEDS, persistedPatient.getMeds());
        Assert.assertEquals(NEW_CONDITIONS, persistedPatient.getConditions());
        Assert.assertEquals(NEW_ALLERGIES, persistedPatient.getAllergies());
        Assert.assertEquals(NEW_DIET, persistedPatient.getDiet());
        Assert.assertEquals(NEW_HOBBIES, persistedPatient.getHobbies());
        Assert.assertEquals(NEW_JOB, persistedPatient.getJob());
        Assert.assertEquals(NEW_INSURANCE_ID, persistedPatient.getInsurance().getId());
        Assert.assertEquals(NEW_INSURANCENUM, persistedPatient.getInsuranceNumber());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:patientDetails-notNull.sql"})
    public void testUpdateAllNull(){
        final Long PATIENT_ID = TestData.Users.patientId;
        // final Patient PATIENT = TestData.Users.patient;
        // PATIENT.setId(PATIENT_ID);
        // PATIENT.getPicture().setId(TestData.Images.validImageId);
        // PATIENT.setBloodType(BloodTypeEnum.AB_POSITIVE);
        // PATIENT.setSmokes(true);
        // PATIENT.setDrinks(false);
        // PATIENT.setMeds("nope");
        // PATIENT.setConditions("nope");
        // PATIENT.setAllergies("nope");
        // PATIENT.setDiet("nope");
        // PATIENT.setHobbies("nope");
        // PATIENT.setJob("carpenter");
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        INSURANCE.setId(INSURANCE_ID);
        //PATIENT.setInsurance(INSURANCE);
        final String INSURANCENUM = "12345678";
        //.setInsuranceNumber(INSURANCENUM);

        final String TELEPHONE = TestData.Users.patient.getTelephone();
        final File PIC = TestData.Images.validImage;
        final Long PIC_ID = TestData.Images.validImageId;
        PIC.setId(PIC_ID);
        final LocaleEnum LOCALE = TestData.Users.patient.getLocale();
        final LocalDate BIRTHDATE = TestData.Users.patient.getBirthdate();
        final BloodTypeEnum BLOODTYPE = TestData.PatientDetails.BLOOD_TYPE_1;
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

        patientDao.updatePatient(PATIENT_ID, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        Patient persistedPatient = em.find(Patient.class, PATIENT_ID);

        Assert.assertEquals(TELEPHONE, persistedPatient.getTelephone());
        Assert.assertEquals(PIC_ID, persistedPatient.getPicture().getId());
        Assert.assertEquals(LOCALE, persistedPatient.getLocale());
        Assert.assertEquals(BIRTHDATE, persistedPatient.getBirthdate());
        Assert.assertEquals(BLOODTYPE, persistedPatient.getBloodType());
        Assert.assertEquals(HEIGHT, persistedPatient.getHeight());
        Assert.assertEquals(WEIGHT.doubleValue(), persistedPatient.getWeight().doubleValue(), 0.01);
        Assert.assertEquals(SMOKES, persistedPatient.getSmokes());
        Assert.assertEquals(DRINKS, persistedPatient.getDrinks());
        Assert.assertEquals(MEDS, persistedPatient.getMeds());
        Assert.assertEquals(CONDITIONS, persistedPatient.getConditions());
        Assert.assertEquals(ALLERGIES, persistedPatient.getAllergies());
        Assert.assertEquals(DIET, persistedPatient.getDiet());
        Assert.assertEquals(HOBBIES, persistedPatient.getHobbies());
        Assert.assertEquals(JOB, persistedPatient.getJob());
        Assert.assertEquals(INSURANCE_ID, persistedPatient.getInsurance().getId());
        Assert.assertEquals(INSURANCENUM, persistedPatient.getInsuranceNumber());
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
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNameCount() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long DOC_ID = TestData.Users.doctorId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        int count = patientDao.getAuthDoctorsByPatientIdAndNameCount(PATIENT_ID, DOC_NAME);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertEquals(1, count);
        Assert.assertNotNull(doctorPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNameCountNullName() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        int count = patientDao.getAuthDoctorsByPatientIdAndNameCount(PATIENT_ID, null);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertEquals(1, count);
        Assert.assertNotNull(doctorPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNameCountEmptyName() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        int count = patientDao.getAuthDoctorsByPatientIdAndNameCount(PATIENT_ID, "");
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertEquals(1, count);
        Assert.assertNotNull(doctorPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNameCountNonexistentPatient() {
        final long PATIENT_ID = 0L;

        int count = patientDao.getAuthDoctorsByPatientIdAndNameCount(PATIENT_ID, null);

        Assert.assertEquals(0, count);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNamePage() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        final String DOC_NAME = TestData.Users.doctor.getName();
        final Long DOC_ID = TestData.Users.doctorId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        List<Doctor> docs = patientDao.getAuthDoctorsByPatientIdAndNamePage(PATIENT_ID, DOC_NAME, 1, 1);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(docs);
        Assert.assertFalse(docs.isEmpty());
        Assert.assertEquals(DOC_ID, docs.get(0).getId());
        Assert.assertNotNull(doctorPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNamePageNullName() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        List<Doctor> docs = patientDao.getAuthDoctorsByPatientIdAndNamePage(PATIENT_ID, null, 1, 1);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(docs);
        Assert.assertFalse(docs.isEmpty());
        Assert.assertEquals(DOC_ID, docs.get(0).getId());
        Assert.assertNotNull(doctorPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNamePageEmptyName() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        List<Doctor> docs = patientDao.getAuthDoctorsByPatientIdAndNamePage(PATIENT_ID, "", 1, 1);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(docs);
        Assert.assertFalse(docs.isEmpty());
        Assert.assertEquals(DOC_ID, docs.get(0).getId());
        Assert.assertNotNull(doctorPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNamePageNonexistentPatient() {
        final long PATIENT_ID = 0L;

        List<Doctor> docs = patientDao.getAuthDoctorsByPatientIdAndNamePage(PATIENT_ID, null, 1, 1);

        Assert.assertNotNull(docs);
        Assert.assertTrue(docs.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNamePageInvalidPage() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        List<Doctor> docs = patientDao.getAuthDoctorsByPatientIdAndNamePage(PATIENT_ID, null, 0, 1);

        Assert.assertNotNull(docs);
        Assert.assertTrue(docs.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientIdAndNamePageInvalidPageSize() {
        final Patient PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(TestData.Users.patientId);
        PATIENT.getPicture().setId(TestData.Images.validImageId);

        List<Doctor> docs = patientDao.getAuthDoctorsByPatientIdAndNamePage(PATIENT_ID, null, 1, 0);

        Assert.assertNotNull(docs);
        Assert.assertTrue(docs.isEmpty());
    }

}