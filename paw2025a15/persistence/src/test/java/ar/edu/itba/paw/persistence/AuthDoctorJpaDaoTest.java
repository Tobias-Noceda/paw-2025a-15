package ar.edu.itba.paw.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
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

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate
    // @Test
    // @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorSingleShifts.sql", "classpath:authDoctors.sql"})
    // public void testGetAuthDoctorsByPatientId(){
    //     final Patient PATIENT = TestData.Users.patient;
    //     final long DOCTOR_ID = TestData.Users.doctorId;
    //     final long PICTURE_ID = TestData.Images.validImageId;
    //     final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
    //     INSURANCE1.setId(TestData.Insurances.validInsuranceId);
    //     INSURANCE1.getPicture().setId(PICTURE_ID);
    //     final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
    //     INSURANCE2.setId(TestData.Insurances.validInsurance2Id);
    //     INSURANCE2.getPicture().setId(PICTURE_ID);

    //     List<Doctor> foundDocs = authDoctorDao.getAuthDoctorsByPatientId(PATIENT);
    //     AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOCTOR_ID, PATIENT, AccessLevelEnum.VIEW_BASIC));

    //     Assert.assertFalse(foundDocs.isEmpty());
    //     Assert.assertEquals(1, foundDocs.size());
    //     Assert.assertNotNull(adFound);
    //     Assert.assertEquals(PATIENT, adFound.getPatient());
    //     Assert.assertEquals(DOCTOR_ID, adFound.getDoctor());
    //     Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adFound.getAccessLevel());
    // }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate
    // @Test
    // @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    // public void testGetAuthDoctorsByPatientIdWithoutAuths(){
    //     final Patient PATIENT = TestData.Users.patient;

    //     List<Doctor> foundDocs = authDoctorDao.getAuthDoctorsByPatientId(PATIENT);

    //     Assert.assertTrue(foundDocs.isEmpty());
    // }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate
    // @Test
    // @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    // public void testGetAuthDoctorsByPatientIdNonexistentPatient(){
    //     final Patient PATIENT = TestData.Users.newPatient.getId;

    //     List<Doctor> foundDocs = authDoctorDao.getAuthDoctorsByPatientId(PATIENT);

    //     Assert.assertTrue(foundDocs.isEmpty());
    // }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctor(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT, DOC);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(result);
        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT, adFound.getPatient());
        Assert.assertEquals(DOC, adFound.getDoctor());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testHasAuthDoctorNoAuth(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT, DOC);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentPatient(){
        final Patient PATIENT = TestData.Users.newPatient;
        final Doctor DOC = TestData.Users.doctor;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT, DOC);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentDoctor(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.newDoctor;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT, DOC);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevel(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertTrue(result);
        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT, adFound.getPatient());
        Assert.assertEquals(DOC, adFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testHasAuthDoctorWithAccessLevelNoAuth(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentPatient(){
        final Patient PATIENT = TestData.Users.newPatient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentDoctor(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.newDoctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctor(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT, adFound.getPatient());
        Assert.assertEquals(DOC, adFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentAuth(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT, adFound.getPatient());
        Assert.assertEquals(DOC, adFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentBasicAuth(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        authDoctorDao.authDoctor(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

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
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonExistentBasicAuth(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        authDoctorDao.authDoctor(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

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
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonexistentPatient(){
        final Patient PATIENT = TestData.Users.newPatient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonexistentDoc(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.newDoctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorAllLevelsBasicOnly(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT, DOC);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testUnauthDoctorAllLevelsNoAuth(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT, DOC);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorAllLevelsMultiple(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT, DOC);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorByAccessLevelBasicOnly(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));

        Assert.assertNull(adFound);}

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleSocialErase(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT, adBasicFound.getPatient());
        Assert.assertEquals(DOC, adBasicFound.getDoctor());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adBasicFound.getAccessLevel());
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleBasicErase(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT, DOC, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnums(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL_SOCIAL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        List<AccessLevelEnum> foundAccessLevels = authDoctorDao.getAuthAccessLevelEnums(PATIENT, DOC);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL_SOCIAL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT, adBasicFound.getPatient());
        Assert.assertEquals(DOC, adBasicFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT, adSocialFound.getPatient());
        Assert.assertEquals(DOC, adSocialFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL_SOCIAL, adSocialFound.getAccessLevel());
        Assert.assertFalse(foundAccessLevels.isEmpty());
        Assert.assertEquals(2, foundAccessLevels.size());
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL));
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL_SOCIAL));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthAccessLevelEnumsNoAuth(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT, DOC);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentPatient(){
        final Patient PATIENT = TestData.Users.newPatient;
        final Doctor DOC = TestData.Users.doctor;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT, DOC);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentDoctor(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.newDoctor;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT, DOC);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    public void testAuthDoctorWithLevels(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        int[] results = authDoctorDao.authDoctorWithLevels(PATIENT, DOC, ACCESS_LEVELS);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL2));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT, adBasicFound.getPatient());
        Assert.assertEquals(DOC, adBasicFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT, adSocialFound.getPatient());
        Assert.assertEquals(DOC, adSocialFound.getDoctor());
        Assert.assertEquals(ACCES_LEVEL2, adSocialFound.getAccessLevel());
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorWithLevels(){
        final Patient PATIENT = TestData.Users.patient;
        final Doctor DOC = TestData.Users.doctor;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        int[] results = authDoctorDao.unauthDoctorForLevels(PATIENT, DOC, ACCESS_LEVELS);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC.getId(), PATIENT.getId(), ACCES_LEVEL2));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

}