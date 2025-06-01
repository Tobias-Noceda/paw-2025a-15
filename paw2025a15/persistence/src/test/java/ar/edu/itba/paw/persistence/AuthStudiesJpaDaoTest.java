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
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
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
        final Doctor DOC = TestData.Users.doctor;
        final Study STUDY = TestData.Studies.validStudyWithDate;

        boolean result = authStudiesDao.authStudyForDoctor(STUDY, DOC);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC.getId(), STUDY.getId()));

        Assert.assertTrue(result);
        Assert.assertNotNull(asPersisted);
        Assert.assertEquals(DOC, asPersisted.getDoctor());
        Assert.assertEquals(STUDY, asPersisted.getStudy());
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentDoc(){
        final Doctor FAKE_DOC = new Doctor(); 
        FAKE_DOC.setId(0L);
        final Study STUDY = TestData.Studies.validStudyWithDate;

        boolean result = authStudiesDao.authStudyForDoctor(STUDY, FAKE_DOC);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(FAKE_DOC.getId(), STUDY.getId()));

        Assert.assertFalse(result);
        Assert.assertNull(asPersisted);
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentStudy(){
        final Doctor DOC = TestData.Users.doctor;
        final Study FAKE_STUDY = new Study();
        FAKE_STUDY.setId(0L);

        boolean result = authStudiesDao.authStudyForDoctor(FAKE_STUDY, DOC);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC.getId(), 0L));

        Assert.assertFalse(result);
        Assert.assertNull(asPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testHasAuthStudy(){
        final Doctor DOC = TestData.Users.doctor;
        final Study STUDY = TestData.Studies.validStudyWithDate;

        boolean result = authStudiesDao.hasAuthStudy(STUDY, DOC);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC.getId(), STUDY.getId()));

        Assert.assertTrue(result);
        Assert.assertNotNull(asPersisted);
        Assert.assertEquals(DOC.getId(), asPersisted.getDoctor().getId());
        Assert.assertEquals(STUDY.getId(), asPersisted.getStudy().getId());
    }

    @Test
    public void testHasAuthStudyUnauth(){
        final Doctor DOC = TestData.Users.doctor;
        final Study STUDY = TestData.Studies.validStudyWithDate;

        boolean result = authStudiesDao.hasAuthStudy(STUDY, DOC);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUnauthStudyForDoctorId(){
        final Doctor DOC = TestData.Users.doctor;
        final Study STUDY = TestData.Studies.validStudyWithDate;

        authStudiesDao.unauthStudyForDoctor(STUDY, DOC);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC.getId(), STUDY.getId()));

        Assert.assertNull(asPersisted);
    }

    @Test
    public void testUnauthStudyForDoctorIdUnauth(){
        final Doctor DOC = TestData.Users.doctor;
        final Study STUDY = TestData.Studies.validStudyWithDate;

        authStudiesDao.unauthStudyForDoctor(STUDY, DOC);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC.getId(), STUDY.getId()));

        Assert.assertNull(asPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUnauthAllStudyForDoctorIdAndPatientId(){
        final Doctor DOC = TestData.Users.doctor;
        final Patient PATIENT = TestData.Users.patient;
        final long STUDY1_ID = TestData.Studies.validStudyWithDateId;
        final long STUDY2_ID = TestData.Studies.validStudyWithoutDateId;

        authStudiesDao.unauthAllStudiesForDoctorAndPatient(PATIENT, DOC);
        AuthStudy asPersisted = em.find(AuthStudy.class, new AuthStudyId(DOC.getId(), STUDY1_ID));
        AuthStudy as2Persisted = em.find(AuthStudy.class, new AuthStudyId(DOC.getId(), STUDY2_ID));

        Assert.assertNull(asPersisted);
        Assert.assertNull(as2Persisted);
    }

}