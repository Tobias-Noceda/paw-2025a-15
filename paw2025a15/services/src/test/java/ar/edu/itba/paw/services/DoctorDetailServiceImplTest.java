package ar.edu.itba.paw.services;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;

@RunWith(MockitoJUnitRunner.class)
public class DoctorDetailServiceImplTest {

    private static final long DOC_ID = 1L;
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final DoctorDetail DOC_DETAIL = new DoctorDetail(DOC_ID, DOC_LICENCE, DOC_SPECIALTY);

    @InjectMocks
    private DoctorDetailServiceImpl dds;

    @Mock
    private DoctorDetailDao doctorDetailDaoMock;

    @Test
    public void testCreate(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());
        Mockito.when(doctorDetailDaoMock.create(Mockito.eq(DOC_ID), Mockito.eq(DOC_LICENCE), Mockito.eq(DOC_SPECIALTY))).thenReturn(DOC_DETAIL);

        DoctorDetail dd = dds.create(DOC_ID, DOC_LICENCE, DOC_SPECIALTY);

        Assert.assertNotNull(dd);
        Assert.assertEquals(DOC_DETAIL, dd);
        Mockito.verify(doctorDetailDaoMock).create(Mockito.eq(DOC_ID), Mockito.eq(DOC_LICENCE), Mockito.eq(DOC_SPECIALTY));
    }

    @Test
    public void testCreateExistentPatientDetail(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dds.create(DOC_ID, DOC_LICENCE, DOC_SPECIALTY)
        );

        Mockito.verify(doctorDetailDaoMock, Mockito.never()).create(Mockito.anyLong(), Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testCreateFailure(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());
        Mockito.when(doctorDetailDaoMock.create(Mockito.eq(DOC_ID), Mockito.eq(DOC_LICENCE), Mockito.eq(DOC_SPECIALTY))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
        dds.create(DOC_ID, DOC_LICENCE, DOC_SPECIALTY)
        );
        
        Mockito.verify(doctorDetailDaoMock).create(Mockito.eq(DOC_ID), Mockito.eq(DOC_LICENCE), Mockito.eq(DOC_SPECIALTY));
    }
}
