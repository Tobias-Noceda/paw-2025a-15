/*package ar.edu.itba.paw.persistence;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AuthDoctorJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private AuthDoctorJdbcDao authDoctorDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientId(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOCTOR_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long PICTURE_ID = TestData.Users.doctor.getPictureId();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();

        List<DoctorView> foundDocs = authDoctorDao.getAuthDoctorsByPatientId(PATIENT_ID);

        Assert.assertFalse(foundDocs.isEmpty());
        Assert.assertEquals(1, foundDocs.size());
        Assert.assertEquals(DOCTOR_ID, foundDocs.getFirst().getId());
        Assert.assertEquals(DOC_NAME, foundDocs.getFirst().getName());
        Assert.assertEquals(SPECIALTY, foundDocs.getFirst().getSpecialty());
        Assert.assertEquals(PICTURE_ID, foundDocs.getFirst().getImageId());
        Assert.assertFalse(foundDocs.getFirst().getInsurances().isEmpty());
        Assert.assertEquals(2, foundDocs.getFirst().getInsurances().size());
        Assert.assertTrue(foundDocs.getFirst().getInsurances().contains(INSURANCE1));
        Assert.assertTrue(foundDocs.getFirst().getInsurances().contains(INSURANCE2));
        Assert.assertFalse(foundDocs.getFirst().getWeekdays().isEmpty());
        Assert.assertEquals(1, foundDocs.getFirst().getWeekdays().size());
        Assert.assertTrue(foundDocs.getFirst().getWeekdays().contains(WEEKDAY));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthDoctorsByPatientIdWithoutAuths(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();

        List<DoctorView> foundDocs = authDoctorDao.getAuthDoctorsByPatientId(PATIENT_ID);

        Assert.assertTrue(foundDocs.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthDoctorsByPatientIdNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();

        List<DoctorView> foundDocs = authDoctorDao.getAuthDoctorsByPatientId(PATIENT_ID);

        Assert.assertTrue(foundDocs.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);

        Assert.assertTrue(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testHasAuthDoctorNoAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.Users.newDoctor.getId();

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevel(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertTrue(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testHasAuthDoctorWithAccessLevelNoAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.Users.newDoctor.getId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentBasicAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonExistentBasicAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        Assert.assertThrows(DataIntegrityViolationException.class, () -> {
            authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);});
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonexistentDocNonexistentDoc(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.Users.newDoctor.getId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        Assert.assertThrows(DataIntegrityViolationException.class, () -> {
            authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);});
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorAllLevelsBasicOnly(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d", DOC_ID, PATIENT_ID)));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testUnauthDoctorAllLevelsNoAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d", DOC_ID, PATIENT_ID)));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorAllLevelsMultiple(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d", DOC_ID, PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorByAccessLevelBasicOnly(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleSocialErase(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleBasicErase(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d", DOC_ID, PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnums(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL_SOCIAL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);

        Assert.assertFalse(foundAccessLevels.isEmpty());
        Assert.assertEquals(2, foundAccessLevels.size());
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL));
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL_SOCIAL));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthAccessLevelEnumsNoAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);

        Assert.assertTrue(foundAccessLevels.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);

        Assert.assertTrue(foundAccessLevels.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.Users.newDoctor.getId();

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);

        Assert.assertTrue(foundAccessLevels.isEmpty());
    }

    @Test
    public void testAuthDoctorWithLevels(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        int[] results = authDoctorDao.authDoctorWithLevels(PATIENT_ID, DOC_ID, ACCESS_LEVELS);

        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL2.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorWithLevels(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        int[] results = authDoctorDao.unauthDoctorForLevels(PATIENT_ID, DOC_ID, ACCESS_LEVELS);

        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL2.ordinal())));
    }
}
*/