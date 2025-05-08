package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

@RunWith(MockitoJUnitRunner.class)
public class StudyServiceImplTest {

    private static final long FILE_ID = 1L;
    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_ID, FILE_CONTENT, FILETYPE);

    private static final long PATIENT_ID = 1L;
    private static final PatientDetail PATIENT_DETAIL_EMPTY = new PatientDetail(PATIENT_ID, null, null, null, null, null, null, null, null, null, null, null, null);

    private static final long DOC_ID = 2L;
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
    private static final Study STUDY_NOT_OTHER = new Study(DOC_ID, StudyTypeEnum.ELECTROCARDIOGRAM, COMMENT, FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_UPLOAD_TIME, STUDY_DATE);

    @InjectMocks
    private StudyServiceImpl ss;

    @Mock
    private StudyDao studyDaoMock;

    @Mock
    private PatientDetailService pds;

    @Mock
    private DoctorDetailService dds;
    
    @Mock
    private FileService fs;

    @Test
    public void testCreateWithStudyDate(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID), Mockito.eq(STUDY_DATE))).thenReturn(STUDY_WITH_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITH_DATE, study);
    }

    @Test
    public void testCreateWithoutStudyDate(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(STUDY_WITHOUT_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testCreateWithStudyDateUploaderNotADoctorButThemselves(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_DATE))).thenReturn(STUDY_WITH_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_USER_ID, STUDY_DATE);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITH_DATE, study);
    }

    @Test
    public void testCreateWithoutStudyDateUploaderNotADoctorButThemselves(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_USER_ID))).thenReturn(STUDY_WITHOUT_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_USER_ID);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testCreateWithStudyDateFailure(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID), Mockito.eq(STUDY_DATE))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithoutStudyDateFailure(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentUser(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentUploader(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithStudyDateNonexistentFile(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_DATE)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentUser(){

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentUploader(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithoutStudyDateNonexistentFile(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID)
        );
    }

    @Test
    public void testCreateWithStudyDateNullDate(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(STUDY_USER_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(fs.findById(Mockito.eq(STUDY_FILE_ID))).thenReturn(Optional.of(FILE));
        Mockito.when(studyDaoMock.create(Mockito.eq(STUDYTYPE), Mockito.eq(COMMENT), Mockito.eq(FILE_ID), Mockito.eq(STUDY_USER_ID), Mockito.eq(STUDY_UPLOADER_ID))).thenReturn(STUDY_WITHOUT_DATE);

        Study study = ss.create(STUDYTYPE, COMMENT, STUDY_FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, null);

        Assert.assertNotNull(study);
        Assert.assertEquals(STUDY_WITHOUT_DATE, study);
    }

    @Test
    public void testAuthStudyForDoctorId() {
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(studyDaoMock.hasAuthStudy(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(false);
        Mockito.when(studyDaoMock.authStudyForDoctorId(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(true);

        boolean result = ss.authStudyForDoctorId(STUDY_ID, DOC_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testAuthStudyForDoctorIdFailure() {
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(studyDaoMock.hasAuthStudy(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(false);
        Mockito.when(studyDaoMock.authStudyForDoctorId(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(false);

        boolean result = ss.authStudyForDoctorId(STUDY_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    public void testAuthStudyForDoctorIdAlreadyAuth() {
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(studyDaoMock.hasAuthStudy(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(true);

        boolean result = ss.authStudyForDoctorId(STUDY_ID, DOC_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentStudy() {
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.authStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentDoc() {
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.authStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testUnauthStudyForDoctorIdNonexistentStudy() {
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.unauthStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testUnauthStudyForDoctorIdNonexistentDoc() {
        Mockito.when(studyDaoMock.findStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.unauthStudyForDoctorId(STUDY_ID, DOC_ID)
        );
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

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            ss.getStudyFile(STUDY_ID)
        );
    }

    @Test
    public void testGetFilteredStudies(){
        Mockito.when(studyDaoMock.getStudiesByPatientId(PATIENT_ID)).thenReturn(List.of(STUDY_NOT_OTHER, STUDY_WITH_DATE, STUDY_WITHOUT_DATE));

        List<Study> results = ss.getFilteredStudies(PATIENT_ID, STUDY_NOT_OTHER.getType(), false);

        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(STUDY_NOT_OTHER, results.get(0));
    }

    @Test
    public void testGetFilteredStudiesMultiple(){
        Mockito.when(studyDaoMock.getStudiesByPatientId(PATIENT_ID)).thenReturn(List.of(STUDY_NOT_OTHER, STUDY_WITH_DATE, STUDY_WITHOUT_DATE));

        List<Study> results = ss.getFilteredStudies(PATIENT_ID, STUDYTYPE, false);

        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(STUDY_WITHOUT_DATE));
        Assert.assertTrue(results.contains(STUDY_WITH_DATE));
    }

    @Test
    public void testGetFilteredStudiesNullType(){
        Mockito.when(studyDaoMock.getStudiesByPatientId(PATIENT_ID)).thenReturn(List.of(STUDY_NOT_OTHER, STUDY_WITH_DATE, STUDY_WITHOUT_DATE));

        List<Study> results = ss.getFilteredStudies(PATIENT_ID, null, false);

        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(3, results.size());
        Assert.assertTrue(results.contains(STUDY_WITHOUT_DATE));
        Assert.assertTrue(results.contains(STUDY_WITHOUT_DATE));
        Assert.assertTrue(results.contains(STUDY_NOT_OTHER));
    }
}
