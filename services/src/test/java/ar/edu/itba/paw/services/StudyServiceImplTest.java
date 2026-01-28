package ar.edu.itba.paw.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

@RunWith(MockitoJUnitRunner.class)
public class StudyServiceImplTest {

    private static final Long FILE_ID = 1L;
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
    private static final List<Insurance> DOC_INSURANCES = new ArrayList<>();
    private static final Doctor DOC = new Doctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, FILE, DOC_CREATE_DATE, DOC_LOCALE, DOC_LICENCE, DOC_SPECIALTY, DOC_INSURANCES);
    
    private static final Long STUDY_ID = 1L;
    private static final StudyTypeEnum STUDYTYPE = StudyTypeEnum.OTHER;
    private static final String COMMENT = "comment";
    private static final LocalDateTime STUDY_UPLOAD_TIME = LocalDateTime.now();
    private static final LocalDate STUDY_DATE = LocalDate.parse("2025-04-09");
    private static final Study STUDY_WITH_DATE = new Study(STUDYTYPE, COMMENT, List.of(FILE), PATIENT, DOC, STUDY_UPLOAD_TIME, STUDY_DATE);
    private static final Study STUDY_WITHOUT_DATE = new Study(STUDYTYPE, COMMENT, List.of(FILE), PATIENT, DOC, STUDY_UPLOAD_TIME, STUDY_UPLOAD_TIME.toLocalDate());
    
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
    private PatientService ps;

    @Mock
    private DoctorService ds;

    @Mock
    private EmailService es;

    // @Test
    // public void testCreateWithStudyDate(){
    //     FILE.setId(FILE_ID);
    //     STUDY_WITH_DATE.setId(STUDY_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
    //     Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(List.of(FILE)), Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(STUDY_DATE))).thenReturn(STUDY_WITH_DATE);
    //     Mockito.doNothing().when(es).sendRecievedStudyEmail(Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(List.of(FILE)), Mockito.eq(STUDY_WITH_DATE), Mockito.eq(COMMENT));
    //     Mockito.when(ass.authStudyForDoctorId(Mockito.eq(FILE_ID), Mockito.eq(DOC_ID))).thenReturn(true);

    //     Study study = ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID, STUDY_DATE);

    //     Assert.assertNotNull(study);
    //     Assert.assertEquals(STUDY_WITH_DATE, study);
    // }

    // @Test TODO rehacer tests
    // public void testCreateWithoutStudyDate(){
    //     FILE.setId(FILE_ID);
    //     STUDY_WITHOUT_DATE.setId(STUDY_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
    //     Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(List.of(FILE)), Mockito.eq(PATIENT), Mockito.eq(DOC))).thenReturn(STUDY_WITHOUT_DATE);
    //     Mockito.doNothing().when(es).sendRecievedStudyEmail(Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(List.of(FILE)), Mockito.eq(STUDY_WITHOUT_DATE), Mockito.eq(COMMENT));
    //     Mockito.when(ass.authStudyForDoctorId(Mockito.eq(FILE_ID), Mockito.eq(DOC_ID))).thenReturn(true);

    //     Study study = ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID);

    //     Assert.assertNotNull(study);
    //     Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    // }

    // @Test
    // public void testCreateWithStudyDateUploaderNotADoctorButThemselves(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(List.of(FILE)), Mockito.eq(PATIENT), Mockito.eq(PATIENT), Mockito.eq(STUDY_DATE))).thenReturn(STUDY_WITH_DATE);

    //     Study study = ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, PATIENT_ID, STUDY_DATE);

    //     Assert.assertNotNull(study);
    //     Assert.assertEquals(STUDY_WITH_DATE, study);
    // }

    // @Test
    // public void testCreateWithoutStudyDateUploaderNotADoctorButThemselves(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(List.of(FILE)), Mockito.eq(PATIENT), Mockito.eq(PATIENT))).thenReturn(STUDY_WITHOUT_DATE);

    //     Study study = ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, PATIENT_ID);

    //     Assert.assertNotNull(study);
    //     Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    // }

    // @Test
    // public void testCreateWithStudyDateFailure(){
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
    //     Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(List.of(FILE)), Mockito.eq(PATIENT), Mockito.eq(DOC), Mockito.eq(STUDY_DATE))).thenReturn(null);

    //     Assert.assertThrows(RuntimeException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID, STUDY_DATE)
    //     );
    // }

    // @Test
    // public void testCreateWithoutStudyDateFailure(){
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
    //     Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(List.of(FILE)), Mockito.eq(PATIENT), Mockito.eq(DOC))).thenReturn(null);

    //     Assert.assertThrows(RuntimeException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID)
    //     );
    // }

    // @Test
    // public void testCreateWithStudyDateNonexistentUser(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID, STUDY_DATE)
    //     );
    // }

    // @Test
    // public void testCreateWithStudyDateNonexistentUploader(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID, STUDY_DATE)
    //     );
    // }

    // @Test
    // public void testCreateWithStudyDateUnathDoc(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(false);
        
    //     Assert.assertThrows(UnauthorizedException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID, STUDY_DATE)
    //     );
    // }

    // @Test
    // public void testCreateWithStudyDateNonexistentFile(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID, STUDY_DATE)
    //     );
    // }

    // @Test
    // public void testCreateWithoutStudyDateNonexistentUser(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID)
    //     );
    // }

    // @Test
    // public void testCreateWithoutStudyDateNonexistentUploader(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID)
    //     );
    // }

    // @Test
    // public void testCreateWithoutStudyDateUnauthDoc(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(false);
        
    //     Assert.assertThrows(UnauthorizedException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID)
    //     );
    // }

    // @Test
    // public void testCreateWithoutStudyDateNonexistentFile(){
    //     FILE.setId(FILE_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID)
    //     );
    // }

    // @Test
    // public void testCreateWithStudyDateNullDate(){
    //     FILE.setId(FILE_ID);
    //     STUDY_WITHOUT_DATE.setId(STUDY_ID);
    //     Mockito.when(fs.findById(Mockito.eq(FILE_ID))).thenReturn(Optional.of(FILE));
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(ads.hasAuthDoctor(Mockito.eq(PATIENT_ID), Mockito.eq(DOC_ID))).thenReturn(true);
    //     Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(List.of(FILE)), Mockito.eq(PATIENT), Mockito.eq(DOC))).thenReturn(STUDY_WITHOUT_DATE);
    //     Mockito.when(ass.authStudyForDoctorId(Mockito.eq(FILE_ID), Mockito.eq(DOC_ID))).thenReturn(true);

    //     Study study = ss.create(STUDYTYPE, COMMENT, List.of(FILE), PATIENT_ID, DOC_ID, null);

    //     Assert.assertNotNull(study);
    //     Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    // }

    // @Test TODO rework tests!
    // public void testGetFilteredStudiesNonexistentPatient(){
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.getFilteredStudies(PATIENT_ID, STUDYTYPE, false)
    //     );
    // }

    // @Test
    // public void testGetFilteredStudiesByPatientIdAndDoctorIdNonexistentPatient(){
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.getFilteredStudiesByPatientIdAndDoctorId(PATIENT_ID, DOC_ID, STUDYTYPE, false)
    //     );
    // }

    // @Test
    // public void testGetFilteredStudiesByPatientIdAndDoctorIdNonexistentDoc(){
    //     Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         ss.getFilteredStudiesByPatientIdAndDoctorId(PATIENT_ID, DOC_ID, STUDYTYPE, false)
    //     );
    // }
    
}
