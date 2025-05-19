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

import ar.edu.itba.paw.interfaces.persistence.AuthStudiesDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class AuthStudiesServiceImplTest {

    private static final long FILE_ID = 1L;
    
    private static final long PATIENT_ID = 1L;
    private static final PatientDetail PATIENT_DETAIL = new PatientDetail(PATIENT_ID, null, null, null, null, null, null, null, null, null, null, null, null);

    private static final long DOC_ID = 2L;
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final DoctorDetail DOC_DETAIL = new DoctorDetail(DOC_ID, DOC_LICENCE, DOC_SPECIALTY);

    private static final long STUDY_ID = 1L;
    private static final StudyTypeEnum STUDYTYPE = StudyTypeEnum.OTHER;
    private static final String COMMENT = "comment";
    private static final long STUDY_USER_ID = PATIENT_ID;
    private static final long STUDY_UPLOADER_ID = DOC_ID;
    private static final LocalDateTime STUDY_UPLOAD_TIME = LocalDateTime.now();
    private static final LocalDate STUDY_DATE = LocalDate.parse("2025-04-09");
    private static final Study STUDY_WITH_DATE = new Study(STUDY_ID, STUDYTYPE, COMMENT, FILE_ID, STUDY_USER_ID, STUDY_UPLOADER_ID, STUDY_UPLOAD_TIME, STUDY_DATE);
    
    @InjectMocks
    private AuthStudiesServiceImpl ass;

    @Mock
    private AuthStudiesDao authStudyDaoMock;

    @Mock
    private StudyService ss;

    @Mock
    private PatientDetailService pds;

    @Mock
    private DoctorDetailService dds;

    @Test
    public void testAuthStudyForDoctorId() {
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(authStudyDaoMock.hasAuthStudy(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(false);
        Mockito.when(authStudyDaoMock.authStudyForDoctorId(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(true);

        boolean result = ass.authStudyForDoctorId(STUDY_ID, DOC_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testAuthStudyForDoctorIdFailure() {
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(authStudyDaoMock.hasAuthStudy(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(false);
        Mockito.when(authStudyDaoMock.authStudyForDoctorId(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(false);

        boolean result = ass.authStudyForDoctorId(STUDY_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    public void testAuthStudyForDoctorIdAlreadyAuth() {
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(authStudyDaoMock.hasAuthStudy(Mockito.eq(STUDY_ID), Mockito.eq(DOC_ID))).thenReturn(true);

        boolean result = ass.authStudyForDoctorId(STUDY_ID, DOC_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentStudy() {
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ass.authStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testAuthStudyForDoctorIdNonexistentDoc() {
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ass.authStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testUnauthStudyForDoctorIdNonexistentStudy() {
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ass.unauthStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testUnauthStudyForDoctorIdNonexistentDoc() {
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ass.unauthStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testToggleStudyForDoctorIdNonexistentStudy(){
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ass.toggleStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testToggleStudyForDoctorIdNonexistentDoctor(){
        Mockito.when(ss.getStudyById(Mockito.eq(STUDY_ID))).thenReturn(Optional.of(STUDY_WITH_DATE));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ass.toggleStudyForDoctorId(STUDY_ID, DOC_ID)
        );
    }

    @Test
    public void testUnauthAllStudiesForDoctorIdAndPatientIdNonexistentPatient(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ass.unauthAllStudiesForDoctorIdAndPatientId(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testUnauthAllStudiesForDoctorIdAndPatientIdNonexistentDoc(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT_DETAIL));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ass.unauthAllStudiesForDoctorIdAndPatientId(PATIENT_ID, DOC_ID)
        );
    }
}
*/