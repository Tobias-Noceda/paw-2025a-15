package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.List;

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
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.StudyTypeEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Sql("classpath:studies.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class StudyJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private StudyJdbcDao studyDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateWithoutStudyDate(){
        final StudyTypeEnum TYPE = TestData.Studies.newStudyWithoutDate.getType();
        final String COMMENT = TestData.Studies.newStudyWithoutDate.getComment();
        final long FILE_ID = TestData.Studies.newStudyWithoutDate.getFileId();
        final long USER_ID = TestData.Studies.newStudyWithoutDate.getUserId();
        final long UPLOADER_ID = TestData.Studies.validStudyWithoutDate.getUploaderId();
        final LocalDate ACTUAL_DATE = LocalDate.now();

        Study study = studyDao.create(TYPE, COMMENT, FILE_ID, USER_ID, UPLOADER_ID);

        Assert.assertNotNull(study);
        Assert.assertEquals(TYPE, study.getType());
        Assert.assertEquals(COMMENT, study.getComment());
        Assert.assertEquals(FILE_ID, study.getFileId());
        Assert.assertEquals(USER_ID, study.getUserId());
        Assert.assertEquals(UPLOADER_ID, study.getUploaderId());
        Assert.assertEquals(ACTUAL_DATE, study.getStudyDate());
        Assert.assertEquals(ACTUAL_DATE, study.getUploadDate().toLocalDate());   
    }

    @Test
    public void testCreateWithStudyDate(){
        final StudyTypeEnum TYPE = TestData.Studies.newStudyWithDate.getType();
        final String COMMENT = TestData.Studies.newStudyWithDate.getComment();
        final long FILE_ID = TestData.Studies.newStudyWithDate.getFileId();
        final long USER_ID = TestData.Studies.newStudyWithDate.getUserId();
        final long UPLOADER_ID = TestData.Studies.validStudyWithDate.getUploaderId();
        final LocalDate STUDY_DATE = TestData.Studies.validStudyWithDate.getStudyDate();
        final LocalDate ACTUAL_DATE = LocalDate.now();

        Study study = studyDao.create(TYPE, COMMENT, FILE_ID, USER_ID, UPLOADER_ID, STUDY_DATE);

        Assert.assertNotNull(study);
        Assert.assertEquals(TYPE, study.getType());
        Assert.assertEquals(COMMENT, study.getComment());
        Assert.assertEquals(FILE_ID, study.getFileId());
        Assert.assertEquals(USER_ID, study.getUserId());
        Assert.assertEquals(UPLOADER_ID, study.getUploaderId());
        Assert.assertEquals(STUDY_DATE, study.getStudyDate());
        Assert.assertEquals(ACTUAL_DATE, study.getUploadDate().toLocalDate());   
    }

    @Test
    public void testGetStudiesByPatientId(){
        final long PATIENT_ID = TestData.Users.patient.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;

        List<Study> foundStudies = studyDao.getStudiesByPatientId(PATIENT_ID);

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
    }

    @Test
    public void testGetStudiesByPatientIdNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();

        List<Study> foundStudies = studyDao.getStudiesByPatientId(PATIENT_ID);

        Assert.assertTrue(foundStudies.isEmpty());
    }

}
