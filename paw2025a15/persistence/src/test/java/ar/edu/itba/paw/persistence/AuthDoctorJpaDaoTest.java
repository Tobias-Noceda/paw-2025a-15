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

import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.entities.AuthDoctor;
import ar.edu.itba.paw.models.entities.AuthDoctorId;
import ar.edu.itba.paw.models.entities.Insurance;
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
public class AuthDoctorJpaDaoTest {
    
    @Autowired
    private AuthDoctorJpaDao authDoctorDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientId(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOCTOR_ID = TestData.Users.doctorId;
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long PICTURE_ID = TestData.Images.validImageId;
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
        INSURANCE1.setId(TestData.Insurances.validInsuranceId);
        INSURANCE1.getPicture().setId(PICTURE_ID);
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        INSURANCE2.setId(TestData.Insurances.validInsurance2Id);
        INSURANCE2.getPicture().setId(PICTURE_ID);
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();

        List<DoctorView> foundDocs = authDoctorDao.getAuthDoctorsByPatientId(PATIENT_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOCTOR_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

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
        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT_ID, adFound.getPatient().getId());
        Assert.assertEquals(DOCTOR_ID, adFound.getDoctor().getId());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthDoctorsByPatientIdWithoutAuths(){
        final long PATIENT_ID = TestData.Users.patientId;

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
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(result);
        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT_ID, adFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adFound.getDoctor().getId());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testHasAuthDoctorNoAuth(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatientId;
        final long DOC_ID = TestData.Users.doctorId;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentDoctor(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.newDoctorId;

        boolean result = authDoctorDao.hasAuthDoctor(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevel(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
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
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testHasAuthDoctorWithAccessLevelNoAuth(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentDoctor(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.newDoctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = authDoctorDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertFalse(result);
        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctor(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT_ID, adFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentAuth(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNotNull(adFound);
        Assert.assertEquals(PATIENT_ID, adFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL, adFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentBasicAuth(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT_ID, adBasicFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adBasicFound.getDoctor().getId());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT_ID, adSocialFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adSocialFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL, adSocialFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonExistentBasicAuth(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT_ID, adBasicFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adBasicFound.getDoctor().getId());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT_ID, adSocialFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adSocialFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL, adSocialFound.getAccessLevel());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonexistentDoc(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.newDoctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorAllLevelsBasicOnly(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testUnauthDoctorAllLevelsNoAuth(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertNull(adFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorAllLevelsMultiple(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;

        authDoctorDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorByAccessLevelBasicOnly(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));

        Assert.assertNull(adFound);}

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleSocialErase(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT_ID, adBasicFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adBasicFound.getDoctor().getId());
        Assert.assertEquals(AccessLevelEnum.VIEW_BASIC, adBasicFound.getAccessLevel());
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleBasicErase(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        authDoctorDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_SOCIAL));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnums(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL_SOCIAL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        List<AccessLevelEnum> foundAccessLevels = authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL_SOCIAL));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT_ID, adBasicFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adBasicFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT_ID, adSocialFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adSocialFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL_SOCIAL, adSocialFound.getAccessLevel());
        Assert.assertFalse(foundAccessLevels.isEmpty());
        Assert.assertEquals(2, foundAccessLevels.size());
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL));
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL_SOCIAL));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthAccessLevelEnumsNoAuth(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatientId;
        final long DOC_ID = TestData.Users.doctorId;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentDoctor(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.newDoctorId;

        List<AccessLevelEnum> foundAccessLevels =authDoctorDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC));

        Assert.assertTrue(foundAccessLevels.isEmpty());
        Assert.assertNull(adBasicFound);
    }

    @Test
    public void testAuthDoctorWithLevels(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        int[] results = authDoctorDao.authDoctorWithLevels(PATIENT_ID, DOC_ID, ACCESS_LEVELS);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL2));

        Assert.assertNotNull(adBasicFound);
        Assert.assertEquals(PATIENT_ID, adBasicFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adBasicFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL, adBasicFound.getAccessLevel());
        Assert.assertNotNull(adSocialFound);
        Assert.assertEquals(PATIENT_ID, adSocialFound.getPatient().getId());
        Assert.assertEquals(DOC_ID, adSocialFound.getDoctor().getId());
        Assert.assertEquals(ACCES_LEVEL2, adSocialFound.getAccessLevel());
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorWithLevels(){
        final long PATIENT_ID = TestData.Users.patientId;
        final long DOC_ID = TestData.Users.doctorId;
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL2 = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();
        final List<AccessLevelEnum> ACCESS_LEVELS = List.of(ACCES_LEVEL, ACCES_LEVEL2);

        int[] results = authDoctorDao.unauthDoctorForLevels(PATIENT_ID, DOC_ID, ACCESS_LEVELS);
        AuthDoctor adBasicFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL));
        AuthDoctor adSocialFound = em.find(AuthDoctor.class, new AuthDoctorId(DOC_ID, PATIENT_ID, ACCES_LEVEL2));

        Assert.assertNull(adBasicFound);
        Assert.assertNull(adSocialFound);
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

}
