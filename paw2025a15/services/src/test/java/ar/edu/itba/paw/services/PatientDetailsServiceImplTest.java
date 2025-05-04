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
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@RunWith(MockitoJUnitRunner.class)
public class PatientDetailsServiceImplTest {

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


    @InjectMocks
    private PatientDetailsServiceImpl pds;

    @Mock
    private PatientDetailDao patientDetailDaoMock;


    @Test
    public void testUpdatePatientDetails(){
        Mockito.when(patientDetailDaoMock.getDetailByPatientId(PATIENT_ID)).thenReturn(Optional.of(PATIENT_DETAIL));

        pds.updatePatientDetails(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);
    
        Mockito.verify(patientDetailDaoMock).updatePatientDetails(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);
    }

    @Test
    public void testUpdatePatientDetailsNonexistent(){
        Mockito.when(patientDetailDaoMock.getDetailByPatientId(PATIENT_ID)).thenReturn(Optional.empty());

        pds.updatePatientDetails(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);
    
        Mockito.verify(patientDetailDaoMock, Mockito.never()).updatePatientDetails(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

}
