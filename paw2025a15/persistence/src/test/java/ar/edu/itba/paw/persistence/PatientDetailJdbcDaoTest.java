package ar.edu.itba.paw.persistence;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.BloodTypeEnum;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PatientDetailJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private PatientDetailJdbcDao patientDetailDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        final long PATIENT_ID = TestData.PatientDetails.patientDetail.getPatientId();
        final Integer AGE = TestData.PatientDetails.patientDetail.getAge();
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

        PatientDetail patientDetail = patientDetailDao.create(PATIENT_ID, AGE, BLOOD_TYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);

        Assert.assertNotNull(patientDetail);
        Assert.assertEquals(PATIENT_ID, patientDetail.getPatientId());
        Assert.assertEquals(AGE, patientDetail.getAge());
        Assert.assertEquals(BLOOD_TYPE, patientDetail.getBloodType());
        Assert.assertEquals(HEIGHT, patientDetail.getHeight());
        Assert.assertEquals(WEIGHT, patientDetail.getWeight());
        Assert.assertEquals(SMOKES, patientDetail.getSmokes());
        Assert.assertEquals(DRINKS, patientDetail.getDrinks());
        Assert.assertEquals(MEDS, patientDetail.getMeds());
        Assert.assertEquals(CONDITIONS, patientDetail.getConditions());
        Assert.assertEquals(ALLERGIES, patientDetail.getAllergies());
        Assert.assertEquals(DIET, patientDetail.getDiet());
        Assert.assertEquals(HOBBIES, patientDetail.getHobbies());
        Assert.assertEquals(JOB, patientDetail.getJob());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", String.format("patient_id = %d", PATIENT_ID)));
    }

    @Test
    public void testCreateNotNullBloodType(){
        final long PATIENT_ID = TestData.PatientDetails.patientDetail.getPatientId();
        final Integer AGE = TestData.PatientDetails.patientDetail.getAge();
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

        PatientDetail patientDetail = patientDetailDao.create(PATIENT_ID, AGE, BLOOD_TYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);

        Assert.assertNotNull(patientDetail);
        Assert.assertEquals(PATIENT_ID, patientDetail.getPatientId());
        Assert.assertEquals(AGE, patientDetail.getAge());
        Assert.assertEquals(BLOOD_TYPE, patientDetail.getBloodType());
        Assert.assertEquals(HEIGHT, patientDetail.getHeight());
        Assert.assertEquals(WEIGHT, patientDetail.getWeight());
        Assert.assertEquals(SMOKES, patientDetail.getSmokes());
        Assert.assertEquals(DRINKS, patientDetail.getDrinks());
        Assert.assertEquals(MEDS, patientDetail.getMeds());
        Assert.assertEquals(CONDITIONS, patientDetail.getConditions());
        Assert.assertEquals(ALLERGIES, patientDetail.getAllergies());
        Assert.assertEquals(DIET, patientDetail.getDiet());
        Assert.assertEquals(HOBBIES, patientDetail.getHobbies());
        Assert.assertEquals(JOB, patientDetail.getJob());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", String.format("patient_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testCreateExistentPatient(){
        final long PATIENT_ID =  TestData.PatientDetails.patientDetail.getPatientId();
        final Integer AGE = TestData.PatientDetails.patientDetail.getAge();
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
        //TODO preguntar si el service o el dao es el que tienen que tener la programacion defensiva de esto
        Assert.assertThrows(DuplicateKeyException.class,()->{
            patientDetailDao.create(PATIENT_ID, AGE, BLOOD_TYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);});
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testGetDetailByPatientId(){
        final PatientDetail PATIENT_DETAILS = TestData.PatientDetails.patientDetail;
        final long PATIENT_ID = TestData.PatientDetails.patientDetail.getPatientId();

        Optional<PatientDetail> foundPatientDetail = patientDetailDao.getDetailByPatientId(PATIENT_ID);

        Assert.assertTrue(foundPatientDetail.isPresent());
        Assert.assertEquals(PATIENT_DETAILS, foundPatientDetail.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", String.format("patient_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails-allNull.sql"})
    public void testGetDetailByPatientIdNullDetails(){
        final PatientDetail PATIENT_DETAILS = TestData.PatientDetails.newPatientDetailNullValues;
        final long PATIENT_ID = TestData.PatientDetails.newPatientDetailNullValues.getPatientId();

        Optional<PatientDetail> foundPatientDetail = patientDetailDao.getDetailByPatientId(PATIENT_ID);

        Assert.assertTrue(foundPatientDetail.isPresent());
        Assert.assertEquals(PATIENT_DETAILS, foundPatientDetail.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", String.format("patient_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails-notNull.sql"})
    public void testGetDetailByPatientIdNotNullDetails(){
        final PatientDetail PATIENT_DETAILS = TestData.PatientDetails.newPatientDetailNotNullValues;
        final long PATIENT_ID = TestData.PatientDetails.newPatientDetailNotNullValues.getPatientId();

        Optional<PatientDetail> foundPatientDetail = patientDetailDao.getDetailByPatientId(PATIENT_ID);

        Assert.assertTrue(foundPatientDetail.isPresent());
        Assert.assertEquals(PATIENT_DETAILS, foundPatientDetail.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", String.format("patient_id = %d", PATIENT_ID)));
    }

    @Test
    public void testGetDetailByPatientIdNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.doctor.getId();

        Optional<PatientDetail> foundPatientDetail = patientDetailDao.getDetailByPatientId(PATIENT_ID);

        Assert.assertFalse(foundPatientDetail.isPresent());
        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", String.format("patient_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testUpdatePatientDetails(){
        final long PATIENT_ID =  TestData.PatientDetails.patientDetail.getPatientId();
        final Integer AGE = TestData.PatientDetails.patientDetail.getAge();
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

        final Integer NEW_AGE = TestData.PatientDetails.newPatientDetail.getAge();
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

        patientDetailDao.updatePatientDetails(PATIENT_ID, NEW_AGE, NEW_BLOOD_TYPE, NEW_HEIGHT, NEW_WEIGHT, NEW_SMOKES, NEW_DRINKS, NEW_MEDS, NEW_CONDITIONS, NEW_ALLERGIES, NEW_DIET, NEW_HOBBIES, NEW_JOB);

        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", 
        String.format(Locale.US, 
            "patient_id = %d AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s)",
            PATIENT_ID, 
            AGE == null ? "patient_age IS NULL" : "patient_age = " + AGE,
            BLOOD_TYPE == null ? "patient_blood_type IS NULL" : "patient_blood_type = " + BLOOD_TYPE.ordinal(),
            HEIGHT == null ? "patient_height IS NULL" : String.format(Locale.US, "ROUND(patient_height, 3) = %.3f", HEIGHT),
            WEIGHT == null ? "patient_weight IS NULL" : String.format(Locale.US, "ROUND(patient_weight, 3) = %.3f", WEIGHT),
            SMOKES == null ? "patient_smokes IS NULL" : "patient_smokes = " + SMOKES,
            DRINKS == null ? "patient_drinks IS NULL" : "patient_drinks = " + DRINKS,
            MEDS == null ? "patient_meds IS NULL" : "patient_meds = '" + MEDS + "'",
            CONDITIONS == null ? "patient_conditions IS NULL" : "patient_conditions = '" + CONDITIONS + "'",
            ALLERGIES == null ? "patient_allergies IS NULL" : "patient_allergies = '" + ALLERGIES + "'",
            DIET == null ? "patient_diet IS NULL" : "patient_diet = '" + DIET + "'",
            HOBBIES == null ? "patient_hobbies IS NULL" : "patient_hobbies = '" + HOBBIES + "'",
            JOB == null ? "patient_job IS NULL" : "patient_job = '" + JOB + "'"
        )));
        
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", 
        String.format(Locale.US, 
            "patient_id = %d AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s)",
            PATIENT_ID, 
            NEW_AGE == null ? "patient_age IS NULL" : "patient_age = " + NEW_AGE,
            NEW_BLOOD_TYPE == null ? "patient_blood_type IS NULL" : "patient_blood_type = " + NEW_BLOOD_TYPE.ordinal(),
            NEW_HEIGHT == null ? "patient_height IS NULL" : String.format(Locale.US, "ROUND(patient_height, 2) = %.2f", NEW_HEIGHT),
            NEW_WEIGHT == null ? "patient_weight IS NULL" : String.format(Locale.US, "ROUND(patient_weight, 2) = %.2f", NEW_WEIGHT),
            NEW_SMOKES == null ? "patient_smokes IS NULL" : "patient_smokes = " + NEW_SMOKES,
            NEW_DRINKS == null ? "patient_drinks IS NULL" : "patient_drinks = " + NEW_DRINKS,
            NEW_MEDS == null ? "patient_meds IS NULL" : "patient_meds = '" + NEW_MEDS + "'",
            NEW_CONDITIONS == null ? "patient_conditions IS NULL" : "patient_conditions = '" + NEW_CONDITIONS + "'",
            NEW_ALLERGIES == null ? "patient_allergies IS NULL" : "patient_allergies = '" + NEW_ALLERGIES + "'",
            NEW_DIET == null ? "patient_diet IS NULL" : "patient_diet = '" + NEW_DIET + "'",
            NEW_HOBBIES == null ? "patient_hobbies IS NULL" : "patient_hobbies = '" + NEW_HOBBIES + "'",
            NEW_JOB == null ? "patient_job IS NULL" : "patient_job = '" + NEW_JOB + "'"
        )));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testUpdatePatientDetailsNullNewBloodType(){
        final long PATIENT_ID =  TestData.PatientDetails.patientDetail.getPatientId();
        final Integer AGE = TestData.PatientDetails.patientDetail.getAge();
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

        final Integer NEW_AGE = TestData.PatientDetails.newPatientDetail.getAge();
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

        patientDetailDao.updatePatientDetails(PATIENT_ID, NEW_AGE, NEW_BLOOD_TYPE, NEW_HEIGHT, NEW_WEIGHT, NEW_SMOKES, NEW_DRINKS, NEW_MEDS, NEW_CONDITIONS, NEW_ALLERGIES, NEW_DIET, NEW_HOBBIES, NEW_JOB);

        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", 
        String.format(Locale.US, 
            "patient_id = %d AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s)",
            PATIENT_ID, 
            AGE == null ? "patient_age IS NULL" : "patient_age = " + AGE,
            BLOOD_TYPE == null ? "patient_blood_type IS NULL" : "patient_blood_type = " + BLOOD_TYPE.ordinal(),
            HEIGHT == null ? "patient_height IS NULL" : String.format(Locale.US, "ROUND(patient_height, 3) = %.3f", HEIGHT),
            WEIGHT == null ? "patient_weight IS NULL" : String.format(Locale.US, "ROUND(patient_weight, 3) = %.3f", WEIGHT),
            SMOKES == null ? "patient_smokes IS NULL" : "patient_smokes = " + SMOKES,
            DRINKS == null ? "patient_drinks IS NULL" : "patient_drinks = " + DRINKS,
            MEDS == null ? "patient_meds IS NULL" : "patient_meds = '" + MEDS + "'",
            CONDITIONS == null ? "patient_conditions IS NULL" : "patient_conditions = '" + CONDITIONS + "'",
            ALLERGIES == null ? "patient_allergies IS NULL" : "patient_allergies = '" + ALLERGIES + "'",
            DIET == null ? "patient_diet IS NULL" : "patient_diet = '" + DIET + "'",
            HOBBIES == null ? "patient_hobbies IS NULL" : "patient_hobbies = '" + HOBBIES + "'",
            JOB == null ? "patient_job IS NULL" : "patient_job = '" + JOB + "'"
        )));
        
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", 
        String.format(Locale.US, 
            "patient_id = %d AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s) AND (%s)",
            PATIENT_ID, 
            NEW_AGE == null ? "patient_age IS NULL" : "patient_age = " + NEW_AGE,
            NEW_BLOOD_TYPE == null ? "patient_blood_type IS NULL" : "patient_blood_type = " + NEW_BLOOD_TYPE.ordinal(),
            NEW_HEIGHT == null ? "patient_height IS NULL" : String.format(Locale.US, "ROUND(patient_height, 2) = %.2f", NEW_HEIGHT),
            NEW_WEIGHT == null ? "patient_weight IS NULL" : String.format(Locale.US, "ROUND(patient_weight, 2) = %.2f", NEW_WEIGHT),
            NEW_SMOKES == null ? "patient_smokes IS NULL" : "patient_smokes = " + NEW_SMOKES,
            NEW_DRINKS == null ? "patient_drinks IS NULL" : "patient_drinks = " + NEW_DRINKS,
            NEW_MEDS == null ? "patient_meds IS NULL" : "patient_meds = '" + NEW_MEDS + "'",
            NEW_CONDITIONS == null ? "patient_conditions IS NULL" : "patient_conditions = '" + NEW_CONDITIONS + "'",
            NEW_ALLERGIES == null ? "patient_allergies IS NULL" : "patient_allergies = '" + NEW_ALLERGIES + "'",
            NEW_DIET == null ? "patient_diet IS NULL" : "patient_diet = '" + NEW_DIET + "'",
            NEW_HOBBIES == null ? "patient_hobbies IS NULL" : "patient_hobbies = '" + NEW_HOBBIES + "'",
            NEW_JOB == null ? "patient_job IS NULL" : "patient_job = '" + NEW_JOB + "'"
        )));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:patientDetails.sql"})
    public void testUpdatePatientDetailsNonexistentPatient(){
        final long PATIENT_ID =  TestData.Users.doctor.getId();

        final Integer NEW_AGE = TestData.PatientDetails.newPatientDetail.getAge();
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
        
        patientDetailDao.updatePatientDetails(PATIENT_ID, NEW_AGE, NEW_BLOOD_TYPE, NEW_HEIGHT, NEW_WEIGHT, NEW_SMOKES, NEW_DRINKS, NEW_MEDS, NEW_CONDITIONS, NEW_ALLERGIES, NEW_DIET, NEW_HOBBIES, NEW_JOB);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "patient_details", String.format("patient_id = %d", PATIENT_ID)));
    }

}
