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

import ar.edu.itba.paw.models.entities.Doctor;
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
        final List<File> FILES = List.of(TestData.Images.validImage);
        FILES.get(0).setId(TestData.Images.validImageId);
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        final User UPLOADER = TestData.Users.patient;
        UPLOADER.setId(TestData.Users.patientId);

        Study study = studyDao.create(TYPE, COMMENT, FILES, PATIENT, UPLOADER);
        Study studyPersisted = em.find(Study.class, study.getId());

        Assert.assertNotNull(studyPersisted);
        Assert.assertEquals(STUDY.getType(), studyPersisted.getType());
        Assert.assertEquals(STUDY.getComment(), studyPersisted.getComment());
        Assert.assertEquals(1, studyPersisted.getFiles().size());
        Assert.assertEquals(STUDY.getFiles().get(0).getId(), studyPersisted.getFiles().get(0).getId());
        Assert.assertEquals(STUDY.getPatient().getId(), studyPersisted.getPatient().getId());
        Assert.assertEquals(STUDY.getUploader().getId(), studyPersisted.getUploader().getId());
        Assert.assertEquals(STUDY.getStudyDate(), studyPersisted.getStudyDate());
    }

    @Test
    public void testCreateWithStudyDate(){
        final Study STUDY = TestData.Studies.validStudyWithDate;
        STUDY.setId(TestData.Studies.validStudyWithDateId);
        final StudyTypeEnum TYPE = TestData.Studies.validStudyWithDate.getType();
        final String COMMENT = TestData.Studies.validStudyWithDate.getComment();
        final List<File> FILES = List.of(TestData.Images.validImage);
        FILES.get(0).setId(TestData.Images.validImageId);
        final Patient PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);
        final User UPLOADER = TestData.Users.patient;
        UPLOADER.setId(TestData.Users.patientId);
        final LocalDate STUDY_DATE = TestData.Studies.validStudyWithDate.getStudyDate();

        Study study = studyDao.create(TYPE, COMMENT, FILES, PATIENT, UPLOADER, STUDY_DATE);
        Study studyPersisted = em.find(Study.class, study.getId());

        Assert.assertNotNull(study);
        Assert.assertNotNull(studyPersisted);
        Assert.assertEquals(STUDY.getType(), studyPersisted.getType());
        Assert.assertEquals(STUDY.getComment(), studyPersisted.getComment());
        Assert.assertEquals(1, studyPersisted.getFiles().size());
        Assert.assertEquals(STUDY.getFiles().get(0).getId(), studyPersisted.getFiles().get(0).getId());
        Assert.assertEquals(STUDY.getPatient().getId(), studyPersisted.getPatient().getId());
        Assert.assertEquals(STUDY.getUploader().getId(), studyPersisted.getUploader().getId());
        Assert.assertEquals(STUDY.getStudyDate(), studyPersisted.getStudyDate());
    }

    @Test
    public void testFindStudyById(){
        final Study STUDY = TestData.Studies.validStudyWithDate;
        final long STUDY_ID = TestData.Studies.validStudyWithDateId;
        STUDY.setId(STUDY_ID);
        STUDY.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY.getPatient().setId(TestData.Users.patientId);
        STUDY.getUploader().setId(TestData.Users.patientId);

        Optional<Study> foundStudy = studyDao.findStudyById(STUDY_ID);

        Assert.assertNotNull(foundStudy);
        Assert.assertTrue(foundStudy.isPresent());
        Assert.assertEquals(STUDY, foundStudy.get());
        Assert.assertEquals(STUDY.getType(), foundStudy.get().getType());
        Assert.assertEquals(STUDY.getComment(), foundStudy.get().getComment());
        Assert.assertEquals(1, foundStudy.get().getFiles().size());
        Assert.assertEquals(STUDY.getFiles().get(0).getId(), foundStudy.get().getFiles().get(0).getId());
        Assert.assertEquals(STUDY.getPatient().getId(), foundStudy.get().getPatient().getId());
        Assert.assertEquals(STUDY.getUploader().getId(), foundStudy.get().getUploader().getId());
        Assert.assertEquals(STUDY.getStudyDate(), foundStudy.get().getStudyDate());
    }

    @Test
    public void testFindStudyByIdNonexistentStudy(){
        final long STUDY_ID = TestData.Studies.newStudyId;

        Optional<Study> foundStudy = studyDao.findStudyById(STUDY_ID);

        Assert.assertFalse(foundStudy.isPresent());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql"})
    public void testGetFilteredStudiesByPatientCount(){
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);
        final Long EXTRASTUDY1_ID = TestData.Studies.extraStudyId;
        final Long EXTRASTUDY2_ID = TestData.Studies.extraStudy2Id;

        int studies = studyDao.getFilteredStudiesByPatientCount(PATIENT, STUDY1.getType());
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());
        Study extraStudy1Persisted = em.find(Study.class, EXTRASTUDY1_ID);
        Study extraStudy2Persisted = em.find(Study.class, EXTRASTUDY2_ID);

        Assert.assertEquals(2, studies);
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
        Assert.assertNotNull(extraStudy1Persisted);
        Assert.assertNotNull(extraStudy2Persisted);
    }

    @Test
    public void testGetFilteredStudiesByPatientCountNullType(){
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        int studies = studyDao.getFilteredStudiesByPatientCount(PATIENT, null);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertEquals(2, studies);
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    public void testGetFilteredStudiesByPatientCountNonexistentPatient(){
        final Long PATIENT = 0L;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        int studies = studyDao.getFilteredStudiesByPatientCount(PATIENT, STUDY1.getType());
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertEquals(0, studies);
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql"})
    public void testGetFilteredStudiesByPatientPage(){
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);
        final Long EXTRASTUDY1_ID = TestData.Studies.extraStudyId;
        final Long EXTRASTUDY2_ID = TestData.Studies.extraStudy2Id;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientPage(PATIENT, STUDY1.getType(), false, 1, 100);
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
    public void testGetFilteredStudiesByPatientPageNullType(){
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientPage(PATIENT, null, true, 1, 100);
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
    public void testGetFilteredStudiesByPatientPageNonexistentPatient(){
        final Long PATIENT = 0L;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientPage(PATIENT, STUDY1.getType(), true, 1, 100);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    public void testGetFilteredStudiesByPatientPageInavlidPage(){
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientPage(PATIENT, STUDY1.getType(), true, 0, 100);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    public void testGetFilteredStudiesByPatientPageInavlidPageSize(){
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientPage(PATIENT, STUDY1.getType(), true, 1, 0);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorCountUnauth(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);
        final Long EXTRASTUDY1_ID = TestData.Studies.extraStudyId;
        final Long EXTRASTUDY2_ID = TestData.Studies.extraStudy2Id;

        int studies = studyDao.getFilteredStudiesByPatientAndDoctorCount(PATIENT, DOC, STUDY1.getType());
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());
        Study extraStudy1Persisted = em.find(Study.class, EXTRASTUDY1_ID);
        Study extraStudy2Persisted = em.find(Study.class, EXTRASTUDY2_ID);

        Assert.assertEquals(0, studies);
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
        Assert.assertNotNull(extraStudy1Persisted);
        Assert.assertNotNull(extraStudy2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorCountAuth(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);
        final Long EXTRASTUDY1_ID = TestData.Studies.extraStudyId;
        final Long EXTRASTUDY2_ID = TestData.Studies.extraStudy2Id;

        int studies = studyDao.getFilteredStudiesByPatientAndDoctorCount(PATIENT, DOC, STUDY1.getType());
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());
        Study extraStudy1Persisted = em.find(Study.class, EXTRASTUDY1_ID);
        Study extraStudy2Persisted = em.find(Study.class, EXTRASTUDY2_ID);

        Assert.assertEquals(2, studies);
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
        Assert.assertNotNull(extraStudy1Persisted);
        Assert.assertNotNull(extraStudy2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorCountAuthNullType(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        int studies = studyDao.getFilteredStudiesByPatientAndDoctorCount(PATIENT, DOC, null);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertEquals(2, studies);
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorCountAuthNonexistentPatient(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = 0L;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        int studies = studyDao.getFilteredStudiesByPatientAndDoctorCount(PATIENT, DOC, STUDY1.getType());
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertEquals(0, studies);
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorCountAuthNonexistentDoc(){
        final Long DOC = 0L;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        int studies = studyDao.getFilteredStudiesByPatientAndDoctorCount(PATIENT, DOC, STUDY1.getType());
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertEquals(0, studies);
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorPageUnauth(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);
        final Long EXTRASTUDY1_ID = TestData.Studies.extraStudyId;
        final Long EXTRASTUDY2_ID = TestData.Studies.extraStudy2Id;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctorPage(PATIENT, DOC, STUDY1.getType(), false, 1, 100);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());
        Study extraStudy1Persisted = em.find(Study.class, EXTRASTUDY1_ID);
        Study extraStudy2Persisted = em.find(Study.class, EXTRASTUDY2_ID);

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
        Assert.assertNotNull(extraStudy1Persisted);
        Assert.assertNotNull(extraStudy2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorPageAuth(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);
        final Long EXTRASTUDY1_ID = TestData.Studies.extraStudyId;
        final Long EXTRASTUDY2_ID = TestData.Studies.extraStudy2Id;

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctorPage(PATIENT, DOC, STUDY1.getType(), false, 1, 100);
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
    public void testGetFilteredStudiesByPatientAndDoctorPageAuthNullType(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctorPage(PATIENT, DOC, null, true, 1, 100);
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
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorPageAuthNonexistentPatient(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = 0L;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctorPage(PATIENT, DOC, STUDY1.getType(), true, 1, 100);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorPageAuthNonexistentDoc(){
        final Long DOC = 0L;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctorPage(PATIENT, DOC, STUDY1.getType(), true, 1, 100);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorPageAuthInvalidPage(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctorPage(PATIENT, DOC, STUDY1.getType(), true, 0, 100);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql"})
    public void testGetFilteredStudiesByPatientAndDoctorPageAuthInvalidPageSize(){
        final Long DOC = TestData.Users.doctorId;
        final Long PATIENT = TestData.Users.patientId;
        final Study STUDY1 = TestData.Studies.validStudyWithDate;
        STUDY1.setId(TestData.Studies.validStudyWithDateId);
        STUDY1.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY1.getPatient().setId(TestData.Users.patientId);
        STUDY1.getUploader().setId(TestData.Users.patientId);
        final Study STUDY2 = TestData.Studies.validStudyWithoutDate;
        STUDY2.setId(TestData.Studies.validStudyWithoutDateId);
        STUDY2.getFiles().get(0).setId(TestData.Images.validImageId);
        STUDY2.getPatient().setId(TestData.Users.patientId);
        STUDY2.getUploader().setId(TestData.Users.patientId);

        List<Study> foundStudies = studyDao.getFilteredStudiesByPatientAndDoctorPage(PATIENT, DOC, STUDY1.getType(), true, 1, 0);
        Study study1Persisted = em.find(Study.class, STUDY1.getId());
        Study study2Persisted = em.find(Study.class, STUDY2.getId());

        Assert.assertTrue(foundStudies.isEmpty());
        Assert.assertNotNull(study1Persisted);
        Assert.assertNotNull(study2Persisted);
    }

    @Test
    public void testDeleteStudy(){
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        boolean result = studyDao.deleteStudy(STUDY_ID);
        Study studyPersisted = em.find(Study.class, STUDY_ID);

        Assert.assertTrue(result);
        Assert.assertNull(studyPersisted);
    }

    @Test
    public void testDeleteStudyNonexistentStudy(){
        final Long STUDY_ID = TestData.Studies.extraStudyId;

        boolean result = studyDao.deleteStudy(STUDY_ID);
        Study studyPersisted = em.find(Study.class, STUDY_ID);

        Assert.assertFalse(result);
        Assert.assertNull(studyPersisted);
    }

    @Test
    public void testIsFileInStudy(){
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Long FILE_ID = TestData.Images.validImageId;

        boolean result = studyDao.isFileInStudy(STUDY_ID, FILE_ID);
        Study studyPersisted = em.find(Study.class, STUDY_ID);

        Assert.assertTrue(result);
        Assert.assertNotNull(studyPersisted);
        Assert.assertEquals(1, studyPersisted.getFiles().size());
        Assert.assertEquals(FILE_ID, studyPersisted.getFiles().get(0).getId());
    }

    @Test
    public void testIsFileInStudyNegative(){
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Long FILE_ID = TestData.Images.validImage2Id;

        boolean result = studyDao.isFileInStudy(STUDY_ID, FILE_ID);
        Study studyPersisted = em.find(Study.class, STUDY_ID);

        Assert.assertFalse(result);
        Assert.assertNotNull(studyPersisted);
        Assert.assertEquals(1, studyPersisted.getFiles().size());
        Assert.assertNotEquals(FILE_ID, studyPersisted.getFiles().get(0).getId());
    }

    @Test
    public void testGetStudyFilesCount() {
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        int count = studyDao.getStudyFilesCount(STUDY_ID);
        Study studyPersisted = em.find(Study.class, STUDY_ID);

        Assert.assertEquals(1, count);
        Assert.assertNotNull(studyPersisted);
    }

    @Test
    public void testGetStudyFilesCountNonexistentStudy() {
        final Long STUDY_ID = 0L;

        int count = studyDao.getStudyFilesCount(STUDY_ID);

        Assert.assertEquals(0, count);
    }

    @Test
    public void testGetStudyFilesPage() {
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Long FILE_ID = TestData.Images.validImageId;
        final File FILE = TestData.Images.validImage;
        FILE.setId(FILE_ID);

        List<File> files = studyDao.getStudyFilesPage(STUDY_ID, 1, 100);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        File filePersisted = em.find(File.class, FILE_ID);

        Assert.assertFalse(files.isEmpty());
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(filePersisted);
        Assert.assertEquals(1, files.size());
        Assert.assertTrue(files.contains(FILE));
    }

    @Test
    public void testGetStudyFilesPageNonexistentStudy() {
        final Long STUDY_ID = 0L;

        List<File> files = studyDao.getStudyFilesPage(STUDY_ID, 1, 100);

        Assert.assertTrue(files.isEmpty());
    }

    @Test
    public void testGetStudyFilesPageInvalidPage() {
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Long FILE_ID = TestData.Images.validImageId;
        final File FILE = TestData.Images.validImage;
        FILE.setId(FILE_ID);

        List<File> files = studyDao.getStudyFilesPage(STUDY_ID, 0, 100);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        File filePersisted = em.find(File.class, FILE_ID);

        Assert.assertTrue(files.isEmpty());
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(filePersisted);
    }

    @Test
    public void testGetStudyFilesPageInvalidPageSize() {
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Long FILE_ID = TestData.Images.validImageId;
        final File FILE = TestData.Images.validImage;
        FILE.setId(FILE_ID);

        List<File> files = studyDao.getStudyFilesPage(STUDY_ID, 1, 0);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        File filePersisted = em.find(File.class, FILE_ID);

        Assert.assertTrue(files.isEmpty());
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(filePersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql", "classpath:extraStudies.sql"})
    public void testGetAuthDoctorsCountAuth(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        int docs = studyDao.getAuthDoctorsCount(STUDY_ID);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        Doctor docPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertEquals(1, docs);
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(docPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql"})
    public void testGetAuthDoctorsCountUnauth(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        int docs = studyDao.getAuthDoctorsCount(STUDY_ID);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        Doctor docPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertEquals(0, docs);
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(docPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql", "classpath:extraStudies.sql"})
    public void testGetAuthDoctorsCountAuthNonexistentStudy(){
        final Long STUDY_ID = 0L;

        int docs = studyDao.getAuthDoctorsCount(STUDY_ID);

        Assert.assertEquals(0, docs);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql", "classpath:extraStudies.sql"})
    public void testGetAuthDoctorsPageAuth(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);

        List<Doctor> docs = studyDao.getAuthDoctorsPage(STUDY_ID, 1, 100);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        Doctor docPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertFalse(docs.isEmpty());
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(docPersisted);
        Assert.assertEquals(1, docs.size());
        Assert.assertTrue(docs.contains(DOC));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:extraStudies.sql"})
    public void testGetAuthDoctorsPageUnauth(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;

        List<Doctor> docs = studyDao.getAuthDoctorsPage(STUDY_ID, 1, 100);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        Doctor docPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertTrue(docs.isEmpty());
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(docPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql", "classpath:extraStudies.sql"})
    public void testGetAuthDoctorsPageAuthNonexistentStudy(){
        final Long STUDY_ID = 0L;

        List<Doctor> docs = studyDao.getAuthDoctorsPage(STUDY_ID, 1, 100);

        Assert.assertTrue(docs.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql", "classpath:extraStudies.sql"})
    public void testGetAuthDoctorsPageAuthInvalidPage(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);

        List<Doctor> docs = studyDao.getAuthDoctorsPage(STUDY_ID, 0, 100);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        Doctor docPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertTrue(docs.isEmpty());
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(docPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:studies.sql", "classpath:authDoctors.sql", "classpath:authStudies.sql", "classpath:extraStudies.sql"})
    public void testGetAuthDoctorsPageAuthInvalidPageSize(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Long STUDY_ID = TestData.Studies.validStudyWithDateId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);

        List<Doctor> docs = studyDao.getAuthDoctorsPage(STUDY_ID, 1, 0);
        Study studyPersisted = em.find(Study.class, STUDY_ID);
        Doctor docPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertTrue(docs.isEmpty());
        Assert.assertNotNull(studyPersisted);
        Assert.assertNotNull(docPersisted);
    }

}