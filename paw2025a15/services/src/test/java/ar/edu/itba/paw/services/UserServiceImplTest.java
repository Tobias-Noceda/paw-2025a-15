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

    private static final long PIC2_ID = 1L;
    private static final byte[] PIC2_CONTENT = "Image2".getBytes();
    private static final FileTypeEnum PIC2_FILE_TYPE = FileTypeEnum.JPEG;
    private static final File PICTURE2 = new File(PIC2_ID, PIC2_CONTENT, PIC2_FILE_TYPE);

    private static final long PATIENT_ID = 1L;
    private static final long PATIENT_PIC_ID = PIC_ID;
    private static final long PATIENT_PIC2_ID = PIC2_ID;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_NAME2 = "gracie";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final String PATIENT_TELEPHONE2 = "1144446666";
    private static final UserRoleEnum PATIENT_ROLE = UserRoleEnum.PATIENT;
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User PATIENT = new User(PATIENT_ID, PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_CREATE_DATE, PATIENT_LOCALE);

    private static final long DOC_ID = 2L;
    private static final long DOC_PIC_ID = PIC_ID;
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


    @InjectMocks
    private UserServiceImpl us;

    @Mock
    private UserDao userDaoMock;

    @Mock
    private FileService fs;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreate(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(Mockito.eq(PATIENT_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(userDaoMock.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_PIC_ID), Mockito.eq(PATIENT_LOCALE))).thenReturn(PATIENT);

        User user = us.create(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(PATIENT, user);
        Mockito.verify(userDaoMock).create(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_PIC_ID), Mockito.eq(PATIENT_LOCALE));
    }

    @Test
    public void testCreateExistentEmail(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.of(PATIENT));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            us.create(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE)
        );

        Mockito.verify(userDaoMock, Mockito.never()).create(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void testCreateFailure(){
        Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(Mockito.eq(PATIENT_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(userDaoMock.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_PIC_ID), Mockito.eq(PATIENT_LOCALE))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
        us.create(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE)
        ).getMessage().contains("Failed to create user for email");
    }

    @Test
    public void testGetUserPicture(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(fs.findById(Mockito.eq(PATIENT_PIC_ID))).thenReturn(Optional.of(PICTURE));

        Optional<File> file = us.getUserPicture(PATIENT_ID);

        Assert.assertTrue(file.isPresent());
        Assert.assertEquals(PICTURE, file.get());
        Mockito.verify(fs).findById(Mockito.eq(PATIENT_PIC_ID));
    }

    @Test
    public void testGetUserPictureNonexistentUser(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Optional<File> file = us.getUserPicture(PATIENT_ID);

        Assert.assertTrue(file.isEmpty());
    }

    @Test
    public void testChangePasswordByID(){
        Mockito.when(passwordEncoder.encode(Mockito.eq(DOC_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));

        us.changePasswordByID(PATIENT_ID, DOC_PASSWORD);

        Mockito.verify(userDaoMock).changePasswordByID(Mockito.eq(PATIENT_ID), Mockito.eq("userPassEnc"));
    }

    @Test
    public void testChangePasswordByIDNonexistentUser(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            us.changePasswordByID(PATIENT_ID, DOC_PASSWORD)
        );

        Mockito.verify(userDaoMock, Mockito.never()).changePasswordByID(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testEditUser(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(fs.findById(Mockito.eq(PATIENT_PIC2_ID))).thenReturn(Optional.of(PICTURE2));

        us.editUser(PATIENT_ID, PATIENT_NAME2, PATIENT_TELEPHONE2, PATIENT_PIC2_ID);

        Mockito.verify(userDaoMock).editUser(Mockito.eq(PATIENT_ID), Mockito.eq(PATIENT_NAME2), Mockito.eq(PATIENT_TELEPHONE2), Mockito.eq(PATIENT_PIC2_ID));
        Mockito.verify(fs).findById(Mockito.eq(PATIENT_PIC2_ID));
    }

    @Test
    public void testEditUserNonexistent(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            us.editUser(PATIENT_ID, PATIENT_NAME2, PATIENT_TELEPHONE2, PATIENT_PIC2_ID)
        );

        Mockito.verify(userDaoMock, Mockito.never()).editUser(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
    }

    @Test
    public void testEditUserNonexistentPicture(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(fs.findById(Mockito.eq(PATIENT_PIC2_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            us.editUser(PATIENT_ID, PATIENT_NAME2, PATIENT_TELEPHONE2, PATIENT_PIC2_ID)
        );

        Mockito.verify(userDaoMock, Mockito.never()).editUser(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
    }

    @Test
    public void testUpdateLocale(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));

        us.updateLocale(PATIENT_ID, PATIENT_LOCALE);

        Mockito.verify(userDaoMock).updateLocale(Mockito.eq(PATIENT_ID), Mockito.eq(PATIENT_LOCALE));
    }

    @Test
    public void testUpdateLocaleNonexistentUser(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            us.updateLocale(PATIENT_ID, PATIENT_LOCALE)
        );

        Mockito.verify(userDaoMock, Mockito.never()).updateLocale(Mockito.anyLong(), Mockito.any());
    }

}
