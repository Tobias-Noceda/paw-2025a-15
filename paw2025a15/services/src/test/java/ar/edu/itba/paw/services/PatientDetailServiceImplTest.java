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
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@RunWith(MockitoJUnitRunner.class)
public class PatientDetailServiceImplTest {

    private static final long PATIENT_ID = 1L;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final UserRoleEnum PATIENT_ROLE = UserRoleEnum.PATIENT;
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User PATIENT = new User(PATIENT_ID, PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_CREATE_DATE, PATIENT_LOCALE);


    private static final Integer AGE = 26;
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
    private static final PatientDetail PATIENT_DETAIL = new PatientDetail(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);
    private static final PatientDetail PATIENT_DETAIL_EMPTY = new PatientDetail(PATIENT_ID, null, null, null, null, null, null, null, null, null, null, null, null);


    @InjectMocks
    private PatientDetailServiceImpl pds;

    @Mock
    private PatientDetailDao patientDetailDaoMock;

    @Mock
    private UserService us;

    @Test
    public void testCreatePatient(){
        Mockito.when(us.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(us.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq(PATIENT_PASSWORD), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_LOCALE))).thenReturn(PATIENT);
        Mockito.when(patientDetailDaoMock.create(Mockito.eq(PATIENT_ID), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(PATIENT_DETAIL_EMPTY);

        User user = pds.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(PATIENT, user);
    }

    @Test
    public void testCreatePatientExistentUser(){
        Mockito.when(us.getUserByEmail(Mockito.eq(PATIENT_EMAIL))).thenReturn(Optional.of(PATIENT));
        
        Assert.assertThrows(IllegalArgumentException.class, () -> 
            pds.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_LOCALE)
        );
    }

    @Test
    public void testCreatePatientPDFailure(){
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

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            pds.updatePatientDetails(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB)
        );
    }

}
