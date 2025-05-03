package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long PIC_ID = 1L;
    private static final byte[] PIC_CONTENT = "Image".getBytes();
    private static final FileTypeEnum PIC_FILE_TYPE = FileTypeEnum.JPEG;
    private static final File PICTURE = new File(PIC_ID, PIC_CONTENT, PIC_FILE_TYPE);

    private static final long PATIENT_ID = 1L;
    private static final long PATIENT_PIC_ID = PIC_ID;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final UserRoleEnum PATIENT_ROLE = UserRoleEnum.PATIENT;
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User PATIENT = new User(PATIENT_ID, PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_CREATE_DATE, PATIENT_LOCALE);

    private static final long DOC_ID = 2L;
    private static final long DOC_PIC_ID = PIC_ID;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "goodgraces";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final UserRoleEnum DOC_ROLE = UserRoleEnum.DOCTOR;
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User DOC = new User(DOC_ID, DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_ROLE, DOC_CREATE_DATE, DOC_LOCALE);
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;


    @InjectMocks
    private UserServiceImpl us;

    @Mock
    private UserDao userDaoMock;

    @Mock
    private FileService fs;

    @Mock
    private PatientDetailService pds;

    @Mock
    private DoctorDetailService dds;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreatePatient(){
        Mockito.when(passwordEncoder.encode(Mockito.eq(PATIENT_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(userDaoMock.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_PIC_ID), Mockito.eq(PATIENT_LOCALE))).thenReturn(PATIENT);

        User user = us.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(PATIENT, user);
        Mockito.verify(pds).create(PATIENT_ID, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void testCreatePatientExistentEmail(){
        Mockito.when(userDaoMock.getUserByEmail(PATIENT_EMAIL)).thenReturn(Optional.of(PATIENT));
        
        User user = us.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE);

        Assert.assertNull(user);
        Mockito.verify(pds, Mockito.never()).create(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testCreateDoctor(){
        Mockito.when(passwordEncoder.encode(Mockito.eq(DOC_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(userDaoMock.create(Mockito.eq(DOC_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(DOC_NAME), Mockito.eq(DOC_TELEPHONE), Mockito.eq(DOC_ROLE), Mockito.eq(DOC_PIC_ID), Mockito.eq(DOC_LOCALE))).thenReturn(DOC);

        User user = us.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, DOC_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(DOC, user);
        Mockito.verify(dds).create(DOC_ID, DOC_LICENCE, DOC_SPECIALTY);
    }

    @Test
    public void testCreateDoctorExistentEmail(){
        Mockito.when(userDaoMock.getUserByEmail(DOC_EMAIL)).thenReturn(Optional.of(DOC));
        
        User user = us.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, DOC_LOCALE);

        Assert.assertNull(user);
        Mockito.verify(dds, Mockito.never()).create(Mockito.anyLong(), Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testGetUserPicture(){
        Mockito.when(userDaoMock.getUserById(PATIENT_ID)).thenReturn(Optional.of(PATIENT));
        Mockito.when(fs.findById(PATIENT_PIC_ID)).thenReturn(Optional.of(PICTURE));

        Optional<File> file = us.getUserPicture(PATIENT_ID);

        Assert.assertTrue(file.isPresent());
        Assert.assertEquals(PICTURE, file.get());
        Mockito.verify(fs).findById(PATIENT_PIC_ID);
    }

    @Test
    public void testGetUserPictureNonexistentUser(){
        Mockito.when(userDaoMock.getUserById(PATIENT_ID)).thenReturn(Optional.empty());

        Optional<File> file = us.getUserPicture(PATIENT_ID);

        Assert.assertTrue(file.isEmpty());
    }

}
