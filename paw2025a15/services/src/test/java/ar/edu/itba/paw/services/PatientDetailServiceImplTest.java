package ar.edu.itba.paw.services;

import java.util.Optional;

import org.junit.Assert;
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
    public void testCreate(){
        Mockito.when(patientDetailDaoMock.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());
        Mockito.when(patientDetailDaoMock.create(Mockito.eq(PATIENT_ID), Mockito.eq(AGE), Mockito.eq(BLOODTYPE), Mockito.eq(HEIGHT), Mockito.eq(WEIGHT), Mockito.eq(SMOKES), Mockito.eq(DRINKS), Mockito.eq(MEDS), Mockito.eq(CONDITIONS), Mockito.eq(ALLERGIES), Mockito.eq(DIET), Mockito.eq(HOBBIES), Mockito.eq(JOB))).thenReturn(PATIENT_DETAIL);

        PatientDetail pd = pds.create(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);

        Assert.assertNotNull(pd);
        Assert.assertEquals(PATIENT_DETAIL, pd);
        Mockito.verify(patientDetailDaoMock).create(Mockito.eq(PATIENT_ID), Mockito.eq(AGE), Mockito.eq(BLOODTYPE), Mockito.eq(HEIGHT), Mockito.eq(WEIGHT), Mockito.eq(SMOKES), Mockito.eq(DRINKS), Mockito.eq(MEDS), Mockito.eq(CONDITIONS), Mockito.eq(ALLERGIES), Mockito.eq(DIET), Mockito.eq(HOBBIES), Mockito.eq(JOB));
    }

    @Test
    public void testCreateExistentPatientDetail(){
        Mockito.when(patientDetailDaoMock.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT_DETAIL));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            pds.create(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB)
        );

        Mockito.verify(patientDetailDaoMock, Mockito.never()).create(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testCreateFailure(){
        Mockito.when(patientDetailDaoMock.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());
        Mockito.when(patientDetailDaoMock.create(Mockito.eq(PATIENT_ID), Mockito.eq(AGE), Mockito.eq(BLOODTYPE), Mockito.eq(HEIGHT), Mockito.eq(WEIGHT), Mockito.eq(SMOKES), Mockito.eq(DRINKS), Mockito.eq(MEDS), Mockito.eq(CONDITIONS), Mockito.eq(ALLERGIES), Mockito.eq(DIET), Mockito.eq(HOBBIES), Mockito.eq(JOB))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            pds.create(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB)
        );
        
        Mockito.verify(patientDetailDaoMock).create(Mockito.eq(PATIENT_ID), Mockito.eq(AGE), Mockito.eq(BLOODTYPE), Mockito.eq(HEIGHT), Mockito.eq(WEIGHT), Mockito.eq(SMOKES), Mockito.eq(DRINKS), Mockito.eq(MEDS), Mockito.eq(CONDITIONS), Mockito.eq(ALLERGIES), Mockito.eq(DIET), Mockito.eq(HOBBIES), Mockito.eq(JOB));
    }

    @Test
    public void testUpdatePatientDetails(){
        Mockito.when(patientDetailDaoMock.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT_DETAIL));

        pds.updatePatientDetails(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB);
    
        Mockito.verify(patientDetailDaoMock).updatePatientDetails(Mockito.eq(PATIENT_ID), Mockito.eq(AGE), Mockito.eq(BLOODTYPE), Mockito.eq(HEIGHT), Mockito.eq(WEIGHT), Mockito.eq(SMOKES), Mockito.eq(DRINKS), Mockito.eq(MEDS), Mockito.eq(CONDITIONS), Mockito.eq(ALLERGIES), Mockito.eq(DIET), Mockito.eq(HOBBIES), Mockito.eq(JOB));
    }

    @Test
    public void testUpdatePatientDetailsNonexistent(){
        Mockito.when(patientDetailDaoMock.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            pds.updatePatientDetails(PATIENT_ID, AGE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB)
        );
    
        Mockito.verify(patientDetailDaoMock, Mockito.never()).updatePatientDetails(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

}
