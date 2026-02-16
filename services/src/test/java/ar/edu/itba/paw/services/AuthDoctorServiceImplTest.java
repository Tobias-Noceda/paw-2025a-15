package ar.edu.itba.paw.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
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
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final LocalDate PATIENT_BIRTHDATE = LocalDate.parse("2001-01-01");
    private static final BigDecimal PATIENT_HEIGHT = BigDecimal.valueOf(1.75);
    private static final BigDecimal PATIENT_WEIGHT = BigDecimal.valueOf(89.00);
    private static final Patient PATIENT = new Patient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, FILE, PATIENT_CREATE_DATE, PATIENT_LOCALE, PATIENT_BIRTHDATE, PATIENT_HEIGHT, PATIENT_WEIGHT);
    
    private static final long DOC_ID = 1L;
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
   
    private static final List<AccessLevelEnum> accessLevels = List.of(AccessLevelEnum.VIEW_SOCIAL, AccessLevelEnum.VIEW_HABITS);

    @InjectMocks
    private AuthDoctorServiceImpl ads;

    @Mock
    private PatientService ps;

    @Mock
    private DoctorService ds;

    @Test
    public void testToggleAuthDoctorNonexistentPatient(){
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.toggleAuthDoctor(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testToggleAuthDoctorNonexistentDoc(){
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.toggleAuthDoctor(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testHasAuthDoctorNonexistentDoctor(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.hasAuthDoctor(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testHasAuthDoctorNonexistentPatient(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.hasAuthDoctor(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testUpdateAuthDoctorNonexistentPatient(){
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.updateAuthDoctor(PATIENT_ID, DOC_ID, accessLevels)
        );
    }

    @Test
    public void testUpdateAuthDoctorNonexistentDoc(){
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.updateAuthDoctor(PATIENT_ID, DOC_ID, accessLevels)
        );
    }

    @Test
    public void testGetAuthAccessLevelEnumsNonexistentDoctor(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testGetAuthAccessLevelEnumsPatient(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID)
        );
    }

}
