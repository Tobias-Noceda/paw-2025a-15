// package ar.edu.itba.paw.services;

// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.junit.Assert;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.MockitoJUnitRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import ar.edu.itba.paw.interfaces.persistence.DoctorDao;
// import ar.edu.itba.paw.interfaces.services.FileService;
// import ar.edu.itba.paw.interfaces.services.InsuranceService;
// import ar.edu.itba.paw.interfaces.services.UserService;
// import ar.edu.itba.paw.models.entities.Doctor;
// import ar.edu.itba.paw.models.entities.File;
// import ar.edu.itba.paw.models.entities.Insurance;
// import ar.edu.itba.paw.models.enums.FileTypeEnum;
// import ar.edu.itba.paw.models.enums.LocaleEnum;
// import ar.edu.itba.paw.models.enums.SpecialtyEnum;
// import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
// import ar.edu.itba.paw.models.exceptions.NotFoundException;

// @RunWith(MockitoJUnitRunner.class)
// public class DoctorServiceImplTest {

//     private static final byte[] FILE_CONTENT = "Image".getBytes();
//     private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
//     private static final File FILE = new File(FILE_CONTENT, FILETYPE);
    
//     private static final long DOC_ID = 1L;
//     private static final String DOC_EMAIL = "sabrina@example.com";
//     private static final String DOC_NAME = "sabrina";
//     private static final String DOC_PASSWORD = "shortandsweet";
//     private static final String DOC_TELEPHONE = "1144445555";
//     private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
//     private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
//     private static final String DOC_LICENCE = "med-licence";
//     private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
//     private static final List<Insurance> DOC_INSURANCES = new ArrayList<>();
//     private static final Doctor DOC = new Doctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, FILE, DOC_CREATE_DATE, DOC_LOCALE, DOC_LICENCE, DOC_SPECIALTY, DOC_INSURANCES);

//     private static final long INSURANCE_ID = 1L;
//     private static final long INSURANCE2_ID = 2L;
//     private static final List<Long> INSURANCES = List.of(INSURANCE_ID, INSURANCE2_ID);

//     @InjectMocks
//     private DoctorServiceImpl ds;

//     @Mock
//     private DoctorDao doctorDaoMock;

//     @Mock
//     private UserService us;

//     @Mock
//     private FileService fs;

//     @Mock
//     private InsuranceService is;

//     @Mock
//     private PasswordEncoder passwordEncoder;

//     @Test
//     public void testCreateDoctorExistentEmail(){
//         Mockito.when(us.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.of(DOC));

//         Assert.assertThrows(AlreadyExistsException.class, () -> 
//             ds.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, INSURANCES, DOC_LOCALE)
//         );
//     }

//     @Test
//     public void testCreateDoctorNonexistentDefaultPic(){
//         Mockito.when(us.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.empty());
//         Mockito.when(fs.findById(1)).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, INSURANCES, DOC_LOCALE)
//         );
//     }

//     @Test
//     public void testCreateDoctorNonexistentInsurance(){
//         Mockito.when(us.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.empty());
//         Mockito.when(fs.findById(1)).thenReturn(Optional.of(FILE));
//         Mockito.when(is.getInsuranceById(INSURANCE_ID)).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, INSURANCES, DOC_LOCALE)
//         );
//     }

//     @Test
//     public void testUpdateDoctorCoveragesNonexistentDoctorNull(){
//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.updateDoctor(null, DOC_TELEPHONE, FILE, DOC_LOCALE, INSURANCES)
//         );
//     }  

//     @Test
//     public void testUpdateDoctorCoveragesNonexistentDoctor(){
//         DOC.setId(DOC_ID);
//         Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.updateDoctor(DOC, DOC_TELEPHONE, FILE, DOC_LOCALE, INSURANCES)
//         );
//     }    

//     @Test
//     public void testUpdateDoctorCoveragesNonexistentInsurance(){
//         DOC.setId(DOC_ID);
//         Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
//         Mockito.when(is.getInsuranceById(INSURANCE_ID)).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.updateDoctor(DOC, DOC_TELEPHONE, FILE, DOC_LOCALE, INSURANCES)
//         );
//     }    

//     @Test
//     public void testGetDoctorsPageByParamsNonexistentInsurance(){
//         Mockito.when(is.getInsuranceById(INSURANCE_ID)).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.getDoctorsPageByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE_ID, null, null, 0, 0)
//         );
//     }

//     @Test
//     public void testGetTotalDoctorsByParamsNonexistentInsurance(){
//         Mockito.when(is.getInsuranceById(INSURANCE_ID)).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE_ID, null)
//         );
//     }

//     @Test
//     public void testGetAuthPatientsPageByDoctorIdAndNameNonexistentDoc(){
//         Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.getAuthPatientsPageByDoctorIdAndName(DOC_ID, DOC_NAME, 0, 0)
//         );
//     }

//     @Test
//     public void testGetAuthPatientsCountByDoctorIdAndNameNonexistentDoc(){
//         Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.getAuthPatientsCountByDoctorIdAndName(DOC_ID, DOC_NAME)
//         );
//     }

//     @Test
//     public void testCreateDoctorVacationNullStartDate(){
//         Assert.assertThrows(IllegalArgumentException.class, () -> 
//             ds.createDoctorVacation(DOC_ID, null, LocalDate.now().plusDays(1))
//         );
//     }

//     @Test
//     public void testCreateDoctorVacationNullEndDate(){
//         Assert.assertThrows(IllegalArgumentException.class, () -> 
//             ds.createDoctorVacation(DOC_ID, LocalDate.now().plusDays(1), null)
//         );
//     }

//     @Test
//     public void testCreateDoctorVacationPastStartDate(){
//         Assert.assertThrows(IllegalArgumentException.class, () -> 
//             ds.createDoctorVacation(DOC_ID, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
//         );
//     }

//     @Test
//     public void testCreateDoctorVacationPastEndDate(){
//         Assert.assertThrows(IllegalArgumentException.class, () -> 
//             ds.createDoctorVacation(DOC_ID, LocalDate.now().plusDays(1), LocalDate.now().minusDays(1))
//         );
//     }

//     @Test
//     public void testCreateDoctorVacationEndDateBeforeStartDate(){
//         Assert.assertThrows(IllegalArgumentException.class, () -> 
//             ds.createDoctorVacation(DOC_ID, LocalDate.now().plusDays(2), LocalDate.now().plusDays(1))
//         );
//     }

//     @Test
//     public void testCreateDoctorVacationNonexistentDoc(){
//         Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.createDoctorVacation(DOC_ID, LocalDate.now().plusDays(1), LocalDate.now().plusDays(1))
//         );
//     }

//     @Test
//     public void testDeleteDoctorVacationNullStartDate(){
//         Assert.assertThrows(IllegalArgumentException.class, () -> 
//             ds.deleteDoctorVacation(DOC_ID, null, LocalDate.now().plusDays(1))
//         );
//     }

//     @Test
//     public void testDeleteDoctorVacationNullEndDate(){
//         Assert.assertThrows(IllegalArgumentException.class, () -> 
//             ds.deleteDoctorVacation(DOC_ID, LocalDate.now().plusDays(1), null)
//         );
//     }

//     @Test
//     public void testDeleteDoctorVacationNonexistentDoc(){
//         Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.deleteDoctorVacation(DOC_ID, LocalDate.now().plusDays(1), LocalDate.now().plusDays(1))
//         );
//     }

//     @Test
//     public void testGetDoctorVacationsFutureNonexistentDoc(){
//         Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.getDoctorVacationsFuture(DOC_ID)
//         );
//     }
//     @Test
//     public void testGetDoctorVacationsPastNonexistentDoc(){
//         Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             ds.getDoctorVacationsPast(DOC_ID)
//         );
//     }

// }