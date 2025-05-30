// package ar.edu.itba.paw.services;

// import java.time.LocalDate;
// import java.util.Optional;

// import org.junit.Assert;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.MockitoJUnitRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import ar.edu.itba.paw.interfaces.persistence.UserDao;
// import ar.edu.itba.paw.interfaces.services.FileService;
// import ar.edu.itba.paw.models.entities.File;
// import ar.edu.itba.paw.models.entities.User;
// import ar.edu.itba.paw.models.enums.FileTypeEnum;
// import ar.edu.itba.paw.models.enums.LocaleEnum;
// import ar.edu.itba.paw.models.enums.UserRoleEnum;
// import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
// import ar.edu.itba.paw.models.exceptions.NotFoundException;

// @RunWith(MockitoJUnitRunner.class)
// public class UserServiceImplTest {

//     private static final long PIC_ID = 1L;
//     private static final byte[] PIC_CONTENT = "Image".getBytes();
//     private static final FileTypeEnum PIC_FILE_TYPE = FileTypeEnum.JPEG;
//     private static final File PICTURE = new File(PIC_CONTENT, PIC_FILE_TYPE);

//     private static final byte[] PIC_CONTENT2 = "Image".getBytes();
//     private static final FileTypeEnum PIC_FILE_TYPE2 = FileTypeEnum.JPEG;
//     private static final File PICTURE2 = new File(PIC_CONTENT2, PIC_FILE_TYPE2);

//     private static final long PATIENT_ID = 1L;
//     private static final long PATIENT_PIC_ID = PIC_ID;
//     private static final String PATIENT_EMAIL = "grace@example.com";
//     private static final String PATIENT_NAME = "grace";
//     private static final String PATIENT_NAME2 = "gracie";
//     private static final String PATIENT_PASSWORD = "goodgraces";
//     private static final String PATIENT_TELEPHONE = "1144445555";
//     private static final String PATIENT_TELEPHONE2 = "1144446666";
//     private static final UserRoleEnum PATIENT_ROLE = UserRoleEnum.PATIENT;
//     private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
//     private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
//     private static final User PATIENT = new User(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PICTURE, PATIENT_CREATE_DATE, PATIENT_LOCALE);

//     private static final String DOC_PASSWORD = "shortandsweet";

//     @InjectMocks
//     private UserServiceImpl us;

//     @Mock
//     private UserDao userDaoMock;

//     @Mock
//     private FileService fs;

//     @Mock
//     private PasswordEncoder passwordEncoder;

//     @Test
//     public void testCreate(){
//         Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
//         Mockito.when(fs.findById(Mockito.eq(PATIENT_PIC_ID))).thenReturn(Optional.of(PICTURE));
//         Mockito.when(passwordEncoder.encode(Mockito.eq(PATIENT_PASSWORD))).thenReturn("userPassEnc");
//         Mockito.when(userDaoMock.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PICTURE), Mockito.eq(PATIENT_LOCALE))).thenReturn(PATIENT);

//         User user = us.create(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE);

//         Assert.assertNotNull(user);
//         Assert.assertEquals(PATIENT, user);
//     }

//     @Test
//     public void testCreateExistentEmail(){
//         Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.of(PATIENT));

//         Assert.assertThrows(AlreadyExistsException.class, () -> 
//             us.create(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE)
//         );
//     }

//     @Test
//     public void testCreateFailure(){
//         Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
//         Mockito.when(fs.findById(Mockito.eq(PATIENT_PIC_ID))).thenReturn(Optional.of(PICTURE));
//         Mockito.when(passwordEncoder.encode(Mockito.eq(PATIENT_PASSWORD))).thenReturn("userPassEnc");
//         Mockito.when(userDaoMock.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PICTURE), Mockito.eq(PATIENT_LOCALE))).thenReturn(null);

//         Assert.assertThrows(RuntimeException.class, () -> 
//             us.create(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE)
//         ).getMessage().contains("Failed to create user for email");
//     }

//     @Test
//     public void testGetUserPicture(){
//         Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
//         Mockito.when(fs.findById(Mockito.eq(PATIENT.getPicture().getId()))).thenReturn(Optional.of(PICTURE));

//         Optional<File> file = us.getUserPicture(PATIENT_ID);

//         Assert.assertTrue(file.isPresent());
//         Assert.assertEquals(PICTURE, file.get());
//     }

//     @Test
//     public void testGetUserPictureNonexistentUser(){
//         Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             us.getUserPicture(PATIENT_ID)
//         );
//     }

//     @Test
//     public void testChangePasswordByIDNonexistentUser(){
//         Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             us.changePasswordByID(PATIENT_ID, DOC_PASSWORD)
//         );
//     }

//     @Test
//     public void testEditUserNonexistent(){
//         Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             us.editUser(PATIENT_ID, PATIENT_NAME2, PATIENT_TELEPHONE2, PICTURE2)
//         );
//     }

//     @Test
//     public void testEditUserNonexistentPicture(){
//         Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
//         Mockito.when(fs.findById(Mockito.eq(PICTURE2.getId()))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             us.editUser(PATIENT_ID, PATIENT_NAME2, PATIENT_TELEPHONE2, PICTURE2)
//         );
//     }

//     @Test
//     public void testUpdateLocaleNonexistentUser(){
//         Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             us.updateLocale(PATIENT_ID, PATIENT_LOCALE)
//         );
//     }

// }