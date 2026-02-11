package ar.edu.itba.paw.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.entities.AuthDoctor;
import ar.edu.itba.paw.models.entities.AuthDoctorId;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AuthDoctorJpaDaoTest {
    
    @Autowired
    private AuthDoctorJpaDao authDoctorDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        TestData.Users.patient.setId(TestData.Users.patientId); // Ensure ID is set before creating AuthDoc
        TestData.Users.doctor.setId(TestData.Users.doctorId); // Ensure ID is set before creating AuthDoc
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctor(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(result);
        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT_ID, adFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adFound.getDoctor().getId());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql"})
    public void testHasAuthDoctorNoAuth(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentPatient(){
        final Long PATIENT_ID = TestData.Users.newPatientId;
        final Long DOC_ID = TestData.Users.doctorId;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentDoctor(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.newDoctorId;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevel(){
        final Patient PATIENT = TestData.Users.patient;
        final Long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(PATIENT_ID);
        final Doctor DOC = TestData.Users.doctor;
        final Long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertTrue(result);
        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT_ID, adFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql"})
    public void testHasAuthDoctorWithAccessLevelNoAuth(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentPatient(){
        final Long PATIENT_ID = TestData.Users.newPatientId;
        final Long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentDoctor(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.newDoctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql"})
    public void testAuthDoctor(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT, adFound.getPatient());
        Assert.assertEquals(DOC, adFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentAuth(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT, adFound.getPatient());
        Assert.assertEquals(DOC, adFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentBasicAuth(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT, adBasicFound.getPatient());
        Assert.assertEquals(DOC, adBasicFound.getDoctor());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT, adSocialFound.getPatient());
        Assert.assertEquals(DOC, adSocialFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adSocialFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql"})
    public void testAuthDoctorNonExistentBasicAuth(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT, adBasicFound.getPatient());
        Assert.assertEquals(DOC, adBasicFound.getDoctor());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT, adSocialFound.getPatient());
        Assert.assertEquals(DOC, adSocialFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adSocialFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql"})
    public void testAuthDoctorNonexistentPatient(){
        final Long PATIENT_ID = TestData.Users.newPatientId;
        final Long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql"})
    public void testAuthDoctorNonexistentDoc(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.newDoctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorAllLevelsBasicOnly(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql"})
    public void testUnauthDoctorAllLevelsNoAuth(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorAllLevelsMultiple(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorByAccessLevelBasicOnly(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNull(adFound);}

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleSocialErase(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT_ID, adBasicFound.getAuthDoctorId().getPatientId());
        Assert.assertEquals(DOC_ID, adBasicFound.getAuthDoctorId().getDoctorId());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adBasicFound.getAccessLevel());
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleBasicErase(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnums(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL_SOCIAL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        List<AccessLevelEnum> foundAccessLevels = authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL_SOCIAL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT_ID, adBasicFound.getAuthDoctorId().getPatientId());
        Assert.assertEquals(DOC_ID, adBasicFound.getAuthDoctorId().getDoctorId());
        Assert.assertEquals(ACCES_LEVEL, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT_ID, adSocialFound.getAuthDoctorId().getPatientId());
        Assert.assertEquals(DOC_ID, adSocialFound.getAuthDoctorId().getDoctorId());
        Assert.assertEquals(ACCES_LEVEL_SOCIAL, adSocialFound.getAccessLevel());
        Assert.assertFalse(foundAccessLevels.isEmpty());
        Assert.assertEquals(2, foundAccessLevels.size());
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL));
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL_SOCIAL));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetAuthAccessLevelEnumsNoAuth(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentPatient(){
        final Long PATIENT_ID = TestData.Users.newPatientId;
        final Long DOC_ID = TestData.Users.doctorId;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentDoctor(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.newDoctorId;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    public void testAuthDoctorWithLevels(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        authDoctorDao.authDoctorWithLevels(PATIENT_ID, DOC_ID, ACCESS_LEVELS);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL2));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT, adBasicFound.getPatient());
        Assert.assertEquals(DOC, adBasicFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT, adSocialFound.getPatient());
        Assert.assertEquals(DOC, adSocialFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL2, adSocialFound.getAccessLevel());
    }

    @Test
    public void testAuthDoctorWithLevelsNonexistentDoc(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = 0L;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        authDoctorDao.authDoctorWithLevels(PATIENT_ID, DOC_ID, ACCESS_LEVELS);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL2));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    public void testAuthDoctorWithLevelsNonexistentPatient(){
        final Long PATIENT_ID = 0L;
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        authDoctorDao.authDoctorWithLevels(PATIENT_ID, DOC_ID, ACCESS_LEVELS);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL2));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorWithLevels(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(PATIENT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        authDoctorDao.unauthDoctorForLevels(PATIENT_ID, DOC_ID, ACCESS_LEVELS);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL2));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testDeauthorizeAllDoctors(){
        final Long PATIENT_ID = TestData.Users.patientId;
        final Long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        authDoctorDao.deauthorizeAllDoctors(PATIENT_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL2));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

}