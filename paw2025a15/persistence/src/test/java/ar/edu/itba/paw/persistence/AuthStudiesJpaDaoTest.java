package ar.edu.itba.paw.persistence;

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
        final long DOC_ID = TestData.Users.doctorId;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = authStudiesDao.authStudyForDoctorId(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertTrue(result);
        Assert.assertNotNull(asPersisted);
        Assert.assertEquals(DOC_ID, asPersisted.getDoctor().getId());
        Assert.assertEquals(STUDY_ID, asPersisted.getStudy().getId());
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentDoc(){
        final long DOC_ID = 0L;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = authStudiesDao.authStudyForDoctorId(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertFalse(result);
        Assert.assertNull(asPersisted);
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentStudy(){
        final long DOC_ID = TestData.Users.doctorId;
        final long STUDY_ID = 0L;

        boolean result = authStudiesDao.authStudyForDoctorId(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertFalse(result);
        Assert.assertNull(asPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testHasAuthStudy(){
        final long DOC_ID = TestData.Users.doctorId;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = authStudiesDao.hasAuthStudy(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertTrue(result);
        Assert.assertNotNull(asPersisted);
        Assert.assertEquals(DOC_ID, asPersisted.getDoctor().getId());
        Assert.assertEquals(STUDY_ID, asPersisted.getStudy().getId());
    }

    @Test
    public void testHasAuthStudyUnauth(){
        final long DOC_ID = TestData.Users.doctorId;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = authStudiesDao.hasAuthStudy(STUDY_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUnauthStudyForDoctorId(){
        final long DOC_ID = TestData.Users.doctorId;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        authStudiesDao.unauthStudyForDoctorId(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertNull(asPersisted);
    }

    @Test
    public void testUnauthStudyForDoctorIdUnauth(){
        final long DOC_ID = TestData.Users.doctorId;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;

        authStudiesDao.unauthStudyForDoctorId(STUDY_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY_ID));

        Assert.assertNull(asPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUnauthAllStudyForDoctorIdAndPatientId(){
        final long DOC_ID = TestData.Users.doctorId;
        final long PATIENT_ID = TestData.Users.patientId;
        final long STUDY1_ID = TestData.Studies.validStudyWithDateId;
        final long STUDY2_ID = TestData.Studies.validStudyWithoutDateId;

        authStudiesDao.unauthAllStudiesForDoctorIdAndPatientId(PATIENT_ID, DOC_ID);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY1_ID));
        AuthStudy as2Persisted = em.find(AuthStudy.class, new AuthStudyId(DOC_ID, STUDY2_ID));

        Assert.assertNull(asPersisted);
        Assert.assertNull(as2Persisted);
    }

}