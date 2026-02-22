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

import ar.edu.itba.paw.models.entities.AuthStudy;
import ar.edu.itba.paw.models.entities.AuthStudyId;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Sql("classpath:studies.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AuthStudiesJpaDaoTest {
    
    @Autowired
    private AuthStudiesJpaDao authStudiesDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testAuthStudyForDoctorId(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = authStudiesDao.authStudyForDoctor(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertTrue(result);
        Assert.assertNotNull(asPersisted);
        Assert.assertEquals(DOC_ID, asPersisted.getAuthStudyId().getDoctorId());
        Assert.assertEquals(STUDY_ID, asPersisted.getAuthStudyId().getStudyId());
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentDoc(){
        final Long DOC_ID = TestData.Users.newDoctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = authStudiesDao.authStudyForDoctor(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertFalse(result);
        Assert.assertNull(asPersisted);
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentStudy(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.newStudyId;

        boolean result = authStudiesDao.authStudyForDoctor(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertFalse(result);
        Assert.assertNull(asPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testHasAuthStudy(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = authStudiesDao.hasAuthStudy(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertTrue(result);
        Assert.assertNotNull(asPersisted);
        Assert.assertEquals(DOC_ID, asPersisted.getDoctor().getId());
        Assert.assertEquals(STUDY_ID, asPersisted.getStudy().getId());
    }

    @Test
    public void testHasAuthStudyUnauth(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = authStudiesDao.hasAuthStudy(STUDY_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUnauthAllStudyForDoctorIdAndPatientId(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long PATIENT_ID = TestData.Users.patientId;
        final long STUDY1_ID = TestData.Studies.validStudyWithDateId;
        final long STUDY2_ID = TestData.Studies.validStudyWithoutDateId;

        authStudiesDao.unauthAllStudiesForDoctorAndPatient(PATIENT_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY1_ID));
        AuthStudy as2Persisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY2_ID));

        Assert.assertNull(asPersisted);
        Assert.assertNull(as2Persisted);
    }

    @Test
    public void testAuthStudyForDoctorIdList(){
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Long DOC_ID = TestData.Users.doctorId;
        List<Long> IDS =List.of(DOC_ID);

        authStudiesDao.authStudyForDoctorIdList(IDS, STUDY_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertNotNull(asPersisted);
        Assert.assertEquals(DOC_ID, asPersisted.getDoctor().getId());
        Assert.assertEquals(STUDY_ID, asPersisted.getStudy().getId());
    }

    @Test
    public void testAuthStudyForDoctorIdListNullList(){
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Long DOC_ID = TestData.Users.doctorId;
        List<Long> IDS = null;

        authStudiesDao.authStudyForDoctorIdList(IDS, STUDY_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertNull(asPersisted);
    }

    @Test
    public void testAuthStudyForDoctorIdListEmptyList(){
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Long DOC_ID = TestData.Users.doctorId;
        List<Long> IDS = List.of();

        authStudiesDao.authStudyForDoctorIdList(IDS, STUDY_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertNull(asPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUunauthStudyForDoctorIdList(){
        final Long DOC_ID = TestData.Users.doctorId;
        final long STUDY1_ID = TestData.Studies.validStudyWithDateId;
        final long STUDY2_ID = TestData.Studies.validStudyWithoutDateId;
        List<Long> DOC_IDS = List.of(DOC_ID);

        authStudiesDao.unauthStudyForDoctorIdList(DOC_IDS, STUDY1_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY1_ID));
        AuthStudy as2Persisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY2_ID));

        Assert.assertNull(asPersisted);
        Assert.assertNotNull(as2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUunauthStudyForDoctorIdListNullList(){
        final Long DOC_ID = TestData.Users.doctorId;
        final long STUDY1_ID = TestData.Studies.validStudyWithDateId;
        final long STUDY2_ID = TestData.Studies.validStudyWithoutDateId;
        List<Long> DOC_IDS = null;

        authStudiesDao.unauthStudyForDoctorIdList(DOC_IDS, STUDY1_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY1_ID));
        AuthStudy as2Persisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY2_ID));

        Assert.assertNotNull(asPersisted);
        Assert.assertNotNull(as2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUunauthStudyForDoctorIdListEmptyList(){
        final Long DOC_ID = TestData.Users.doctorId;
        final long STUDY1_ID = TestData.Studies.validStudyWithDateId;
        final long STUDY2_ID = TestData.Studies.validStudyWithoutDateId;
        List<Long> DOC_IDS = List.of();

        authStudiesDao.unauthStudyForDoctorIdList(DOC_IDS, STUDY1_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY1_ID));
        AuthStudy as2Persisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY2_ID));

        Assert.assertNotNull(asPersisted);
        Assert.assertNotNull(as2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUnauthAllStudiesForAllDocsForPatientId(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long PATIENT_ID = TestData.Users.patientId;
        final long STUDY1_ID = TestData.Studies.validStudyWithDateId;
        final long STUDY2_ID = TestData.Studies.validStudyWithoutDateId;

        authStudiesDao.unauthAllStudiesForAllDocsForPatientId(PATIENT_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY1_ID));
        AuthStudy as2Persisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY2_ID));

        Assert.assertNull(asPersisted);
        Assert.assertNull(as2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql"})
    public void testAuthStudyForAllAuthDoctors(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long PATIENT_ID = TestData.Users.patientId;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        authStudiesDao.authStudyForAllAuthDoctors(STUDY_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertNotNull(asPersisted);
        Assert.assertEquals(PATIENT_ID, asPersisted.getStudy().getPatient().getId());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql"})
    public void testAuthStudyForAllAuthDoctorsDocNotAuth(){
        final Long DOC_ID = TestData.Users.doctorId;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        authStudiesDao.authStudyForAllAuthDoctors(STUDY_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertNull(asPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testDeauthStudyForAllDoctors(){
        final Long DOC_ID = TestData.Users.doctorId;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        authStudiesDao.deauthStudyForAllDoctors(STUDY_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertNull(asPersisted);
    }

}