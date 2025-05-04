package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@RunWith(MockitoJUnitRunner.class)
public class StudyServiceImplTest {

    private static final long FILE_ID = 1L;
    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_ID, FILE_CONTENT, FILETYPE);

    private static final long PATIENT_ID = 1L;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final UserRoleEnum PATIENT_ROLE = UserRoleEnum.PATIENT;
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User PATIENT = new User(PATIENT_ID, PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_CREATE_DATE, PATIENT_LOCALE);

    private static final long DOC_ID = 2L;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final UserRoleEnum DOC_ROLE = UserRoleEnum.DOCTOR;
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User DOC = new User(DOC_ID, DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_ROLE, DOC_CREATE_DATE, DOC_LOCALE);

    private static final long STUDY_ID = 1L;
    private static final StudyTypeEnum STUDYTYPE = StudyTypeEnum.OTHER;
    private static final String COMMENT = "comment";
    private static final long STUDY_FILE_ID = FILE_ID;
    private static final long STUDY_USER_ID = PATIENT_ID;
    private static final long STUDY_UPLOADER_ID = DOC_ID;
    private static final LocalDateTime STUDY_UPLOAD_TIME = LocalDateTime.now();
    private static final LocalDate STUDY_DATE = LocalDate.parse("2025-04-09");
    private static final Study STUDY_WITH_DATE = new Study(STUDY_ID, STUDYTYPE, COMMENT, FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_UPLOAD_TIME, STUDY_DATE);
    private static final Study STUDY_WITHOUT_DATE = new Study(STUDY_ID, STUDYTYPE, COMMENT, FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_UPLOAD_TIME, STUDY_UPLOAD_TIME.toLocalDate());

    @InjectMocks
    private StudyServiceImpl ss;

    @Mock
    private StudyDao studyDaoMock;

    @Mock
    private UserService us;
    
    @Mock
    private FileService fs;

    @Test
    public void testCreateWithStudyDate(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getUserById(STUDY_UPLOADER_ID)).thenReturn(Optional.of(DOC));
        Mockito.when(fs.findById(STUDY_FILE_ID)).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(STUDYTYPE, COMMENT, FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)).thenReturn(STUDY_WITH_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITH_DATE, study);
    }

    @Test
    public void testCreateWithoutStudyDate(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getUserById(STUDY_UPLOADER_ID)).thenReturn(Optional.of(DOC));
        Mockito.when(fs.findById(STUDY_FILE_ID)).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(STUDYTYPE, COMMENT, FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID)).thenReturn(STUDY_WITHOUT_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCreateWithStudyDateNonexistentUser(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.empty());

        ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCreateWithStudyDateNonexistentUploader(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getUserById(STUDY_UPLOADER_ID)).thenReturn(Optional.empty());

        ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCreateWithStudyDateNonexistentFile(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getUserById(STUDY_UPLOADER_ID)).thenReturn(Optional.of(DOC));
        Mockito.when(fs.findById(STUDY_FILE_ID)).thenReturn(Optional.empty());

        ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCreateWithoutStudyDateNonexistentUser(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.empty());

        ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCreateWithoutStudyDateNonexistentUploader(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getUserById(STUDY_UPLOADER_ID)).thenReturn(Optional.empty());

        ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCreateWithoutStudyDateNonexistentFile(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getUserById(STUDY_UPLOADER_ID)).thenReturn(Optional.of(DOC));
        Mockito.when(fs.findById(STUDY_FILE_ID)).thenReturn(Optional.empty());

        ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID);
    }

    @Test
    public void testCreateWithStudyDateNullDate(){
        Mockito.when(us.getUserById(STUDY_USER_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getUserById(STUDY_UPLOADER_ID)).thenReturn(Optional.of(DOC));
        Mockito.when(fs.findById(STUDY_FILE_ID)).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(STUDYTYPE, COMMENT, FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID)).thenReturn(STUDY_WITHOUT_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, null);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testGetStudiesByPatientId(){
        List<Study> expectedStudies = Collections.singletonList(STUDY_WITH_DATE);
        Mockito.when(us.getUserById(PATIENT_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(studyDaoMock.getStudiesByPatientId(PATIENT_ID)).thenReturn(expectedStudies);

        List<Study> studies = ss.getStudiesByPatientId(PATIENT_ID);

        Assert.assertFalse(studies.isEmpty());
        Assert.assertEquals(1, studies.size());
        Assert.assertEquals(STUDY_WITH_DATE, studies.get(0));
        Mockito.verify(studyDaoMock).getStudiesByPatientId(PATIENT_ID);
    }

    @Test
    public void testGetStudiesByPatientIdEmpty(){
        Mockito.when(us.getUserById(PATIENT_ID)).thenReturn(Optional.of(PATIENT));
    Mockito.when(studyDaoMock.getStudiesByPatientId(PATIENT_ID)).thenReturn(Collections.emptyList());

        List<Study> studies = ss.getStudiesByPatientId(PATIENT_ID);

        Assert.assertTrue(studies.isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetStudiesByPatientIdNonexistentUser(){
        Mockito.when(us.getUserById(PATIENT_ID)).thenReturn(Optional.empty());

        ss.getStudiesByPatientId(PATIENT_ID);
    }
}
