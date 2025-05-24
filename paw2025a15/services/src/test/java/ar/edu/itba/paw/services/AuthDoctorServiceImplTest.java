package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.AuthDoctorDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.PatientDetail;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class AuthDoctorServiceImplTest {

    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_CONTENT, FILETYPE);

    private static final long PATIENT_ID = 1L;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final UserRoleEnum PATIENT_ROLE = UserRoleEnum.PATIENT;
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User PATIENT = new User(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, FILE, PATIENT_CREATE_DATE, PATIENT_LOCALE);
    private static final PatientDetail PATIENT_DETAIL_EMPTY = new PatientDetail(PATIENT, null, null, null, null, null, null, null, null, null, null, null, null);

    private static final long DOC_ID = 2L;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final UserRoleEnum DOC_ROLE = UserRoleEnum.DOCTOR;
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static User DOC = new User(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_ROLE, FILE, DOC_CREATE_DATE, DOC_LOCALE);
   
    private static final List<AccessLevelEnum> accessLevels = List.of(AccessLevelEnum.VIEW_SOCIAL, AccessLevelEnum.VIEW_HABITS);

    @InjectMocks
    private AuthDoctorServiceImpl ads;

    @Mock
    private AuthDoctorDao authDoctorMock;

    @Mock
    private PatientDetailService pds;

    @Mock
    private DoctorDetailService dds;

    @Mock
    private UserService us;

    @Test
    public void testToggleAuthDoctorNonexistentPatient(){
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.toggleAuthDoctor(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testToggleAuthDoctorNonexistentDoc(){
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.toggleAuthDoctor(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testUpdateAuthDoctorNonexistentPatient(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.updateAuthDoctor(PATIENT_ID, DOC_ID, accessLevels)
        );
    }

    @Test
    public void testUpdateAuthDoctorNonexistentDoc(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.updateAuthDoctor(PATIENT_ID, DOC_ID, accessLevels)
        );
    }

}
