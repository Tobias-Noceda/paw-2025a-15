/*package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

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
    private static final PatientDetail PATIENT_DETAIL_EMPTY = new PatientDetail(PATIENT_ID, null, null, null, null, null, null, null, null, null, null, null, null);

    private static final long DOC_ID = 2L;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final UserRoleEnum DOC_ROLE = UserRoleEnum.DOCTOR;
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User DOC = new User(DOC_ID, DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_ROLE, DOC_CREATE_DATE, DOC_LOCALE);
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final DoctorDetail DOC_DETAIL = new DoctorDetail(DOC_ID, DOC_LICENCE, DOC_SPECIALTY);

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
    private PatientDetailService pds;

    @Mock
    private DoctorDetailService dds;

    @Mock
    private AuthDoctorService ads;

    @Mock
    private AuthStudiesService ass;
    
    @Mock
    private FileService fs;

    @Mock
    private UserService us;

    @Mock
    private EmailService es;

    @Test
    public void testCreateWithStudyDate(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(true);
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID), Mockito.eq(STUDY_DATE))).thenReturn(STUDY_WITH_DATE);
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.doNothing().when(es).sendRecievedStudyEmail(Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(FILE), Mockito.eq(STUDY_WITH_DATE), Mockito.eq(COMMENT));
        Mockito.when(ass.authStudyForDoctorId(Mockito.eq(STUDY_FILE_ID), Mockito.eq(DOC_ID))).thenReturn(true);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITH_DATE, study);
    }

    @Test
    public void testCreateWithoutStudyDate(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(true);
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(STUDY_WITHOUT_DATE);
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.doNothing().when(es).sendRecievedStudyEmail(Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(FILE), Mockito.eq(STUDY_WITHOUT_DATE), Mockito.eq(COMMENT));
        Mockito.when(ass.authStudyForDoctorId(Mockito.eq(STUDY_FILE_ID), Mockito.eq(DOC_ID))).thenReturn(true);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testCreateWithStudyDateUploaderNotADoctorButThemselves(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_DATE))).thenReturn(STUDY_WITH_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_USER_ID, STUDY_DATE);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITH_DATE, study);
    }

    @Test
    public void testCreateWithoutStudyDateUploaderNotADoctorButThemselves(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_USER_ID))).thenReturn(STUDY_WITHOUT_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_USER_ID);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testCreateWithStudyDateFailure(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(true);
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID), Mockito.eq(STUDY_DATE))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithoutStudyDateFailure(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(true);
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentUser(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentUploader(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(UnauthorizedException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithStudyDateUnathDoc(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(false);

        Assert.assertThrows(UnauthorizedException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentFile(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(true);
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentUser(){

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentUploader(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(UnauthorizedException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithoutStudyDateUnauthDoc(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(false);

        Assert.assertThrows(UnauthorizedException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentFile(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(true);
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithStudyDateNullDate(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(true);
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(STUDY_WITHOUT_DATE);
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ass.authStudyForDoctorId(Mockito.eq(STUDY_FILE_ID), Mockito.eq(DOC_ID))).thenReturn(true);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, STUDY_USER_ID, STUDY_UPLOADER_ID, null);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testGetStudyFile(){
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));

        Optional<File> file = ss.getStudyFile(STUDY_ID);

        Assert.assertTrue(file.isPresent());
        Assert.assertEquals(FILE, file.get());
    }

    @Test
    public void testGetStudyFileNonexistentStudy(){
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(AlreadyExistsException.class, () -> 
            ss.getStudyFile(STUDY_ID)
        );
    }
}
*/