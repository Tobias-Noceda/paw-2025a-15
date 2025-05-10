package ar.edu.itba.paw.persistence;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Sql("classpath:studies.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AuthStudiesJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private AuthStudiesJdbcDao authStudiesDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testAuthStudyForDoctorId(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;

        boolean result = authStudiesDao.authStudyForDoctorId(STUDY1.getId(), DOC_ID);

        Assert.assertTrue(result);
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_studies", String.format("doctor_id = %d AND study_id = %d", DOC_ID, STUDY1.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testHasAuthStudy(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long STUDY1_ID = TestData.Studies.validStudyWithDate.getId();

        boolean result = authStudiesDao.hasAuthStudy(STUDY1_ID, DOC_ID);

        Assert.assertTrue(result);
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_studies", String.format("doctor_id = %d AND study_id = %d", DOC_ID, STUDY1_ID)));
    }

    @Test
    public void testHasAuthStudyUnauth(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long STUDY1_ID = TestData.Studies.validStudyWithDate.getId();

        boolean result = authStudiesDao.hasAuthStudy(STUDY1_ID, DOC_ID);

        Assert.assertFalse(result);
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_studies", String.format("doctor_id = %d AND study_id = %d", DOC_ID, STUDY1_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUnauthStudyForDoctorId(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long STUDY1_ID = TestData.Studies.validStudyWithDate.getId();

        authStudiesDao.unauthStudyForDoctorId(STUDY1_ID, DOC_ID);

        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_studies", String.format("doctor_id = %d AND study_id = %d", DOC_ID, STUDY1_ID)));
    }

    @Test
    public void testUnauthStudyForDoctorIdUnauth(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long STUDY1_ID = TestData.Studies.validStudyWithDate.getId();

        authStudiesDao.unauthStudyForDoctorId(STUDY1_ID, DOC_ID);

        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_studies", String.format("doctor_id = %d AND study_id = %d", DOC_ID, STUDY1_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testUnauthAllStudyForDoctorIdAndPatientId(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long PATIENT_ID = TestData.Users.patient.getId();
        final long STUDY1_ID = TestData.Studies.validStudyWithDate.getId();
        final long STUDY2_ID = TestData.Studies.validStudyWithoutDate.getId();

        authStudiesDao.unauthAllStudiesForDoctorIdAndPatientId(PATIENT_ID, DOC_ID);

        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_studies", String.format("doctor_id = %d AND study_id = %d", DOC_ID, STUDY1_ID)));
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_studies", String.format("doctor_id = %d AND study_id = %d", DOC_ID, STUDY2_ID)));
    }

}
