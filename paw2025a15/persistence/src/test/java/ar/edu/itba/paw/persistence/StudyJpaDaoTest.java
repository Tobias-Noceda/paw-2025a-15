package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")  
@Sql("classpath:studies.sql")            
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class StudyJpaDaoTest {
    
    @Autowired
    private StudyJpaDao studyDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql"})
    public void testCreateWithoutStudyDate(){
        final Study STUDY = TestData.Studies.newStudyWithoutDate;
        STUDY.setId(TestData.Studies.newStudyId);
        final StudyTypeEnum TYPE = TestData.Studies.newStudyWithoutDate.getType();
        final String COMMENT = TestData.Studies.newStudyWithoutDate.getComment();
        final File FILE = TestData.Images.validImage;
        FILE.setId(TestData.Images.validImageId);
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        final User UPLOADER = TestData.Users.patient;
        UPLOADER.setId(TestData.Users.patientId);

        Study study = studyDao.create(TYPE, COMMENT, FILE, PATIENT, UPLOADER);
        Study studyPersisted = em.find(Study.class, study.getId());

        Assert.assertNotNull(studyPersisted);
        Assert.assertEquals(STUDY.getType(), studyPersisted.getType());
        Assert.assertEquals(STUDY.getComment(), studyPersisted.getComment());
        Assert.assertEquals(STUDY.getFile(), studyPersisted.getFile());
        Assert.assertEquals(STUDY.getPatient(), studyPersisted.getPatient());
        Assert.assertEquals(STUDY.getUploader(), studyPersisted.getUploader());
        Assert.assertEquals(STUDY.getStudyDate(), studyPersisted.getStudyDate());
    }

    @Test
    public void testCreateWithStudyDate(){
        final Study STUDY = TestData.Studies.validStudyWithDate;
        STUDY.setId(TestData.Studies.validStudyWithDateId);
        final StudyTypeEnum TYPE = TestData.Studies.validStudyWithDate.getType();
        final String COMMENT = TestData.Studies.validStudyWithDate.getComment();
        final File FILE = TestData.Images.validImage;
        FILE.setId(TestData.Images.validImageId);
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        final User UPLOADER = TestData.Users.patient;
        UPLOADER.setId(TestData.Users.patientId);
        final LocalDate STUDY_DATE = TestData.Studies.validStudyWithDate.getStudyDate();

        Study study = studyDao.create(TYPE, COMMENT, FILE, PATIENT, UPLOADER, STUDY_DATE);
        Study studyPersisted = em.find(Study.class, study.getId());

        Assert.assertNotNull(study);
        Assert.assertNotNull(studyPersisted);
        Assert.assertEquals(STUDY.getType(), studyPersisted.getType());
        Assert.assertEquals(STUDY.getComment(), studyPersisted.getComment());
        Assert.assertEquals(STUDY.getFile(), studyPersisted.getFile());
        Assert.assertEquals(STUDY.getPatient(), studyPersisted.getPatient());
        Assert.assertEquals(STUDY.getUploader(), studyPersisted.getUploader());
        Assert.assertEquals(STUDY.getStudyDate(), studyPersisted.getStudyDate());
    }

    @Test
    public void testFindStudyById(){
        final Study STUDY = TestData.Studies.validStudyWithDate;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;
        STUDY.setId(STUDY_ID);
        STUDY.getFile().setId(TestData.Images.validImageId);
        STUDY.getPatient().setId(TestData.Users.patientId);
        STUDY.getUploader().setId(TestData.Users.patientId);

        Optional<Study> foundStudy = studyDao.findStudyById(STUDY_ID);

        Assert.assertNotNull(foundStudy);
        Assert.assertTrue(foundStudy.isPresent());
        Assert.assertEquals(STUDY, foundStudy.get());
    }

    @Test
    public void testFindStudyByIdNonexistentStudy(){
        final long STUDY_ID = TestData.Studies.newStudyId;

        Optional<Study> foundStudy = studyDao.findStudyById(STUDY_ID);

        Assert.assertFalse(foundStudy.isPresent());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql"})
    public void testGetFilteredStudiesByPatientId(){
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFile().setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFile().setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);
        final Long EXTRASTUDY1_ID = TestData.Studies.extraStudyId;
        final Long EXTRASTUDY2_ID = TestData.Studies.extraStudy2Id;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatient(PATIENT, STUDY1.getType(), false);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());
        Study extraStudy1Persisted = em.find(Study.class, EXTRASTUDY1_ID);
        Study extraStudy2Persisted = em.find(Study.class, EXTRASTUDY2_ID);

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
        Assert.assertNotNull(extraStudy1Persisted);
        Assert.assertNotNull(extraStudy2Persisted);
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
    }

    @Test
    public void testGetFilteredStudiesByPatientIdNullType(){
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFile().setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFile().setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatient(PATIENT, null, true);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
    }

    @Test
    public void testGetFilteredStudiesByPatientIdNonexistentPatient(){
        final Long PATIENT = TestData.Users.newPatientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatient(PATIENT, STUDY1.getType(), false);

        Assert.assertTrue(foundStudies.isEmpty());
    }

    @Test
    public void testGetFilteredStudiesByPatientIdAndDoctorIdUnauth(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFile().setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFile().setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctor(PATIENT, DOC, STUDY1.getType(), false);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql", "classpath:extraStudies.sql"})
    public void testGetFilteredStudiesByPatientIdAndDoctorIdAuth(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFile().setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFile().setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);
        final long EXTRASTUDY1_ID = TestData.Studies.extraStudyId;
        final long EXTRASTUDY2_ID = TestData.Studies.extraStudy2Id;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctor(PATIENT, DOC, STUDY1.getType(), false);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());
        Study extraStudy1Persisted = em.find(Study.class, EXTRASTUDY1_ID);
        Study extraStudy2Persisted = em.find(Study.class, EXTRASTUDY2_ID);

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
        Assert.assertNotNull(extraStudy1Persisted);
        Assert.assertNotNull(extraStudy2Persisted);
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientIdAndDoctorIdAuthNullType(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFile().setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFile().setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctor(PATIENT, DOC, null, true);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertFalse(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
        Assert.assertEquals(2, foundStudies.size());
        Assert.assertTrue(foundStudies.contains(STUDY1));
        Assert.assertTrue(foundStudies.contains(STUDY2));
    }
}