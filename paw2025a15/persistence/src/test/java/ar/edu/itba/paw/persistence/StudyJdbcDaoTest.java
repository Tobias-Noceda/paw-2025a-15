/*package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
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
    public void testFindStudyById(){
        final Study STUDY = TestData.Studies.validStudyWithDate;
        final long STUDY_ID = TestData.Studies.validStudyWithDate.getId();

        Optional<Study> foundStudy = studyDao.findStudyById(STUDY_ID);

        Assert.assertTrue(foundStudy.isPresent());
        Assert.assertEquals(STUDY, foundStudy.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("study_id = %d", STUDY_ID)));

    }

    @Test
    public void testFindStudyByIdNonexistentStudy(){
        final long STUDY_ID = TestData.Studies.newStudyWithDate.getId();

        Optional<Study> foundStudy = studyDao.findStudyById(STUDY_ID);

        Assert.assertFalse(foundStudy.isPresent());
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
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    public void testGetStudiesByPatientIdNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();

        List<Study> foundStudies = studyDao.getStudiesByPatientId(PATIENT_ID);

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    public void testGetStudiesByPatientIdAndDoctorIdUnauth(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long PATIENT_ID = TestData.Users.patient.getId();

        List<Study> foundStudies = studyDao.getStudiesByPatientIdAndDoctorId(PATIENT_ID, DOC_ID);

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetStudiesByPatientIdAndDoctorIdAuth(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long PATIENT_ID = TestData.Users.patient.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;

        List<Study> foundStudies = studyDao.getStudiesByPatientIdAndDoctorId(PATIENT_ID, DOC_ID);

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql"})
    public void testGetFilteredStudiesByPatientId(){
        final long PATIENT_ID = TestData.Users.patient.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientId(PATIENT_ID, STUDY1.getType(), false);

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d AND study_type = %d", PATIENT_ID, STUDY1.getType().ordinal())));
        Assert.assertEquals(4, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    public void testGetFilteredStudiesByPatientIdNullType(){
        final long PATIENT_ID = TestData.Users.patient.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientId(PATIENT_ID, null, true);

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    public void testGetFilteredStudiesByPatientIdNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientId(PATIENT_ID, STUDY1.getType(), false);


        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    public void testGetFilteredStudiesByPatientIdAndDoctorIdUnauth(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long PATIENT_ID = TestData.Users.patient.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientIdAndDoctorId(PATIENT_ID, DOC_ID, STUDY1.getType(), false);

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql", "classpath:extraStudies.sql"})
    public void testGetFilteredStudiesByPatientIdAndDoctorIdAuth(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long PATIENT_ID = TestData.Users.patient.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientIdAndDoctorId(PATIENT_ID, DOC_ID, STUDY1.getType(), false);

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d AND study_type = %d", PATIENT_ID, STUDY1.getType().ordinal())));
        Assert.assertEquals(4, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientIdAndDoctorIdAuthNullType(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final long PATIENT_ID = TestData.Users.patient.getId();
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientIdAndDoctorId(PATIENT_ID, DOC_ID, null, true);

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "studies", String.format("user_id = %d", PATIENT_ID)));
    }

}
*/