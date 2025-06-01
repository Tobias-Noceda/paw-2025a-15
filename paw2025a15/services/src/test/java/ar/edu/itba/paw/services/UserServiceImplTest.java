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
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_CONTENT, FILETYPE);

    private static final byte[] PIC_CONTENT = "Image".getBytes();
    private static final FileTypeEnum PIC_FILE_TYPE = FileTypeEnum.JPEG;
    private static final File PICTURE = new File(PIC_CONTENT, PIC_FILE_TYPE);

    private static final byte[] PIC_CONTENT2 = "Image".getBytes();
    private static final FileTypeEnum PIC_FILE_TYPE2 = FileTypeEnum.JPEG;
    private static final File PICTURE2 = new File(PIC_CONTENT2, PIC_FILE_TYPE2);

    private static final long PATIENT_ID = 1L;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_NAME2 = "gracie";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final String PATIENT_TELEPHONE2 = "1144446666";
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final LocalDate PATIENT_BIRTHDATE = LocalDate.parse("2001-01-01");
    private static final BigDecimal PATIENT_HEIGHT = BigDecimal.valueOf(1.75);
    private static final BigDecimal PATIENT_WEIGHT = BigDecimal.valueOf(89.00);
    private static final Patient PATIENT = new Patient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PICTURE, PATIENT_CREATE_DATE, PATIENT_LOCALE, PATIENT_BIRTHDATE, PATIENT_HEIGHT, PATIENT_WEIGHT);
    
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final List<Insurance> DOC_INSURANCES = new ArrayList<>();
    private static final List<Long> DOC_INSURANCE_IDS = new ArrayList<>();
    private static final Doctor DOC = new Doctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, FILE, DOC_CREATE_DATE, DOC_LOCALE, DOC_LICENCE, DOC_SPECIALTY, DOC_INSURANCES);
    
    @InjectMocks
    private PatientDetailServiceImpl pds;

    @Mock
    private UserDao userDaoMock;

    @Mock
    private PatientDetailDao PatientDetailDaoMock;

    @Mock
    private DoctorDetailDao doctorDetailDaoMock;

    @Mock
    private FileService fs;

    @Mock
    private UserService us;

    @Mock
    private DoctorDetailServiceImpl dds;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreatePatient(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
        //Mockito.when(fs.findById(Mockito.eq(PATIENT_PIC_ID))).thenReturn(Optional.of(PICTURE));
        Mockito.when(passwordEncoder.encode(Mockito.eq(PATIENT_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(PatientDetailDaoMock.createPatient(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PICTURE), Mockito.eq(PATIENT_LOCALE), Mockito.eq(PATIENT_BIRTHDATE), Mockito.eq(PATIENT_HEIGHT), Mockito.eq(PATIENT_WEIGHT))).thenReturn(PATIENT);

        Patient user = pds.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_LOCALE, PATIENT_BIRTHDATE, PATIENT_HEIGHT, PATIENT_WEIGHT);

        Assert.assertNotNull(user);
        Assert.assertEquals(PATIENT, user);
    }

    @Test
    public void testCreatePatientExistentEmail(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.of(PATIENT));

        Assert.assertThrows(AlreadyExistsException.class, () -> 
            pds.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_LOCALE, PATIENT_BIRTHDATE, PATIENT_HEIGHT, PATIENT_WEIGHT)
        );
    }
/*
    @Test
    public void testCreatePatientFailure(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(fs.findById(Mockito.eq(PATIENT_PIC_ID))).thenReturn(Optional.of(PICTURE));
        Mockito.when(passwordEncoder.encode(Mockito.eq(PATIENT_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(userDaoMock.createPatient(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PICTURE), Mockito.eq(PATIENT_LOCALE), Mockito.eq(PATIENT_BIRTHDATE), Mockito.eq(PATIENT_HEIGHT), Mockito.eq(PATIENT_WEIGHT))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            us.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE,  PICTURE, PATIENT_LOCALE, PATIENT_BIRTHDATE, PATIENT_HEIGHT, PATIENT_WEIGHT)
        ).getMessage().contains("Failed to create user for email");
    }*/

    @Test
    public void testCreateDoctor(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.empty());
        //Mockito.when(fs.findById(Mockito.eq(DOC_PIC_ID))).thenReturn(Optional.of(PICTURE));
        Mockito.when(passwordEncoder.encode(Mockito.eq(DOC_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(doctorDetailDaoMock.createDoctor(Mockito.eq(DOC_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(DOC_NAME), Mockito.eq(DOC_TELEPHONE), Mockito.eq(PICTURE), Mockito.eq(DOC_LOCALE), Mockito.eq(DOC_LICENCE), Mockito.eq(DOC_SPECIALTY), Mockito.eq(DOC_INSURANCES))).thenReturn(DOC);

        Doctor user = dds.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, DOC_INSURANCE_IDS, DOC_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(DOC, user);
    }

    @Test
    public void testCreateDoctorExistentEmail(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(AlreadyExistsException.class, () -> 
            dds.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, DOC_INSURANCE_IDS, DOC_LOCALE)
        );
    }
/*
    @Test
    public void testCreateDoctorFailure(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(fs.findById(Mockito.eq(DOC_PIC_ID))).thenReturn(Optional.of(PICTURE));
        Mockito.when(passwordEncoder.encode(Mockito.eq(DOC_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(userDaoMock.createDoctor(Mockito.eq(DOC_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(DOC_NAME), Mockito.eq(DOC_TELEPHONE), Mockito.eq(PICTURE), Mockito.eq(DOC_LOCALE), Mockito.eq(DOC_LICENCE), Mockito.eq(DOC_SPECIALTY))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            us.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE,  PICTURE, DOC_LOCALE, DOC_LICENCE, DOC_SPECIALTY)
        ).getMessage().contains("Failed to create user for email");
    }*/

    // TODO: revisar, pero con hibernate creo que no tiene sentido
    // @Test
    // public void testGetUserPicture(){
    //     Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
    //     Mockito.when(fs.findById(Mockito.eq(PATIENT.getPicture().getId()))).thenReturn(Optional.of(PICTURE));

    //     Optional<File> file = fs.getUserPicture(PATIENT);

    //     Assert.assertTrue(file.isPresent());
    //     Assert.assertEquals(PICTURE, file.get());
    // }

    // TODO: revisar, pero con hibernate creo que no tiene sentido
    // @Test
    // public void testGetUserPictureNonexistentUser(){
    //     Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         us.getUserPicture(PATIENT_ID)
    //     );
    // }

    @Test
    public void testChangePasswordByIDNonexistentUser(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            us.changePasswordByID(PATIENT_ID, DOC_PASSWORD)
        );
    }

    // TODO: revisar, pero esto creo que ahora quedó en updateDoctor/updatePatient
    // @Test
    // public void testUpdateLocaleNonexistentUser(){
    //     Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

    //     Assert.assertThrows(NotFoundException.class, () -> 
    //         us.updateLocale(PATIENT_ID, PATIENT_LOCALE)
    //     );
    // }

}