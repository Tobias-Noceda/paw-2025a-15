package ar.edu.itba.paw.services;

import java.math.BigDecimal;
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
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

@RunWith(MockitoJUnitRunner.class)
public class StudyServiceImplTest {

    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_CONTENT, FILETYPE);

    private static final long PATIENT_ID = 1L;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final LocalDate PATIENT_BIRTHDATE = LocalDate.parse("2001-01-01");
    private static final BigDecimal PATIENT_HEIGHT = BigDecimal.valueOf(1.75);
    private static final BigDecimal PATIENT_WEIGHT = BigDecimal.valueOf(89.00);
    private static final Patient PATIENT = new Patient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, FILE, PATIENT_CREATE_DATE, PATIENT_LOCALE, PATIENT_BIRTHDATE, PATIENT_HEIGHT, PATIENT_WEIGHT);
    
    private static final long DOC_ID = 2L;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final Doctor DOC = new Doctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, FILE, DOC_CREATE_DATE, DOC_LOCALE, DOC_LICENCE, DOC_SPECIALTY);
    
    private static final long STUDY_ID = 1L;
    private static final StudyTypeEnum STUDYTYPE = StudyTypeEnum.OTHER;
    private static final String COMMENT = "comment";
    private static final LocalDateTime STUDY_UPLOAD_TIME = LocalDateTime.now();
    private static final LocalDate STUDY_DATE = LocalDate.parse("2025-04-09");
    private static final Study STUDY_WITH_DATE = new Study(STUDYTYPE, COMMENT, FILE, PATIENT, DOC, STUDY_UPLOAD_TIME, STUDY_DATE);
    private static final Study STUDY_WITHOUT_DATE = new Study(STUDYTYPE, COMMENT, FILE, PATIENT, DOC, STUDY_UPLOAD_TIME, STUDY_UPLOAD_TIME.toLocalDate());
    
    @InjectMocks
    private StudyServiceImpl ss;

    @Mock
    private StudyDao studyDaoMock;

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
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE), Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(STUDY_DATE))).thenReturn(STUDY_WITH_DATE);
        Mockito.doNothing().when(es).sendRecievedStudyEmail(Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(FILE), Mockito.eq(STUDY_WITH_DATE), Mockito.eq(COMMENT));
        Mockito.when(ass.authStudyForDoctorId(Mockito.eq(FILE.getId()), Mockito.eq(DOC_ID))).thenReturn(true);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID, STUDY_DATE);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITH_DATE, study);
    }

    @Test
    public void testCreateWithoutStudyDate(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE), Mockito.eq(PATIENT), Mockito.eq(DOC))).thenReturn(STUDY_WITHOUT_DATE);
        Mockito.doNothing().when(es).sendRecievedStudyEmail(Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(FILE), Mockito.eq(STUDY_WITHOUT_DATE), Mockito.eq(COMMENT));
        Mockito.when(ass.authStudyForDoctorId(Mockito.eq(FILE.getId()), Mockito.eq(DOC_ID))).thenReturn(true);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testCreateWithStudyDateUploaderNotADoctorButThemselves(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE), Mockito.eq(PATIENT), Mockito.eq(PATIENT), Mockito.eq(STUDY_DATE))).thenReturn(STUDY_WITH_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, PATIENT_ID, STUDY_DATE);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITH_DATE, study);
    }

    @Test
    public void testCreateWithoutStudyDateUploaderNotADoctorButThemselves(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE), Mockito.eq(PATIENT), Mockito.eq(PATIENT))).thenReturn(STUDY_WITHOUT_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, PATIENT_ID);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testCreateWithStudyDateFailure(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE), Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(STUDY_DATE))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithoutStudyDateFailure(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE), Mockito.eq(PATIENT), Mockito.eq(DOC))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentUser(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentUploader(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithStudyDateUnathDoc(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(false);
        
        Assert.assertThrows(UnauthorizedException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentFile(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentUser(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentUploader(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testCreateWithoutStudyDateUnauthDoc(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(false);
        
        Assert.assertThrows(UnauthorizedException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentFile(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testCreateWithStudyDateNullDate(){
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));
        Mockito.when(us.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(us.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE), Mockito.eq(PATIENT), Mockito.eq(DOC))).thenReturn(STUDY_WITHOUT_DATE);
        Mockito.when(ass.authStudyForDoctorId(Mockito.eq(FILE.getId()), Mockito.eq(DOC_ID))).thenReturn(true);

        Study study = ss.create(STUDYTYPE, COMMENT, FILE, PATIENT_ID, DOC_ID, null);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testGetStudyFile(){
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(fs.findById(Mockito.eq(FILE.getId()))).thenReturn(Optional.of(FILE));

        Optional<File> file = ss.getStudyFile(STUDY_ID);

        Assert.assertTrue(file.isPresent());
        Assert.assertEquals(FILE, file.get());
    }

    @Test
    public void testGetStudyFileNonexistentStudy(){
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ss.getStudyFile(STUDY_ID)
        );
    }
}
