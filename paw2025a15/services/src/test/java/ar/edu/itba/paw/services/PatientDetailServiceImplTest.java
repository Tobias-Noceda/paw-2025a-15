package ar.edu.itba.paw.services;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;

@RunWith(MockitoJUnitRunner.class)
public class PatientDetailServiceImplTest {

    private static final long PATIENT_ID = 1L;

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
    private PatientDetailServiceImpl pds;

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
