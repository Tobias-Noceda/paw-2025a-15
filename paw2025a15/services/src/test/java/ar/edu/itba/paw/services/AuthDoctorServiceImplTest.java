package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.AuthDoctorDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class AuthDoctorServiceImplTest {

    private static final long PATIENT_ID = 1L;
    private static final PatientDetail PATIENT_DETAIL_EMPTY = new PatientDetail(PATIENT_ID, null, null, null, null, null, null, null, null, null, null, null, null);

    private static final long DOC_ID = 2L;

    private static final List<AccessLevelEnum> accessLevels = List.of(AccessLevelEnum.VIEW_SOCIAL, AccessLevelEnum.VIEW_HABITS);

    @InjectMocks
    private AuthDoctorServiceImpl ads;

    @Mock
    private AuthDoctorDao authDoctorMock;

    @Mock
    private PatientDetailService pds;

    @Mock
    private DoctorDetailService dds;

    @Test
    public void testToggleAuthDoctorNonexistentPatient(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.toggleAuthDoctor(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testToggleAuthDoctorNonexistentDoc(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.toggleAuthDoctor(PATIENT_ID, DOC_ID)
        );
    }

    @Test
    public void testUpdateAuthDoctorNonexistentPatient(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.updateAuthDoctor(PATIENT_ID, DOC_ID, accessLevels)
        );
    }

    @Test
    public void testUpdateAuthDoctorNonexistentDoc(){
        Mockito.when(pds.getDetailByPatientId(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT_DETAIL_EMPTY));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ads.updateAuthDoctor(PATIENT_ID, DOC_ID, accessLevels)
        );
    }

}
