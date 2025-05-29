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

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.PatientDetail;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class PatientDetailServiceImplTest {

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
    private static User PATIENT = new User(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, FILE, PATIENT_CREATE_DATE, PATIENT_LOCALE);
    
    private static final LocalDate BIRTHDATE = LocalDate.parse("2000-01-01");
    private static final BloodTypeEnum BLOODTYPE = BloodTypeEnum.AB_POSITIVE;
    private static final Double HEIGHT = 1.50;
    private static final Double WEIGHT = 56.00;
    private static final Boolean SMOKES = false;
    private static final Boolean DRINKS = true;
    private static final String MEDS = "none";
    private static final String CONDITIONS = "none";
    private static final String ALLERGIES = "none";
    private static final String DIET = "food";
    private static final String HOBBIES = "sing";
    private static final String JOB = "carpenter";
    private static final PatientDetail PATIENT_DETAIL_EMPTY = new PatientDetail(PATIENT, null, null, null, null, null, null, null, null, null, null, null, null);


    @InjectMocks
    private PatientDetailServiceImpl pds;

    @Mock
    private PatientDetailDao patientDetailDaoMock;

    @Mock
    private UserService us;

    @Test
    public void testCreatePatient(){
        PATIENT.setId(PATIENT_ID);
        Mockito.when(us.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(us.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq(PATIENT_PASSWORD), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_LOCALE))).thenReturn(PATIENT);
        Mockito.when(patientDetailDaoMock.create(Mockito.eq(PATIENT_ID), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(PATIENT_DETAIL_EMPTY);

        User user = pds.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(PATIENT, user);
    }

    @Test
    public void testCreatePatientExistentUser(){
        PATIENT.setId(PATIENT_ID);
        Mockito.when(us.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.of(PATIENT));
        
        Assert.assertThrows(AlreadyExistsException.class, () -> 
            pds.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_LOCALE)
        );
    }

    @Test
    public void testCreatePatientUSFailure(){
        PATIENT.setId(PATIENT_ID);
        Mockito.when(us.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(us.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq(PATIENT_PASSWORD), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_LOCALE))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            pds.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_LOCALE)
        ).getMessage().contains("Failed to create user for email");
    }

    @Test
    public void testCreatePatientPDFailure(){
        PATIENT.setId(PATIENT_ID);
        Mockito.when(us.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(us.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq(PATIENT_PASSWORD), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_LOCALE))).thenReturn(PATIENT);
        Mockito.when(patientDetailDaoMock.create(Mockito.eq(PATIENT_ID), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            pds.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_LOCALE)
        ).getMessage().contains("Failed to create patient details for userId");
    }

    @Test
    public void testUpdatePatientDetailsNonexistent(){
        Mockito.when(patientDetailDaoMock.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            pds.updatePatientDetails(PATIENT_ID, BIRTHDATE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB)
        );
    }

}
