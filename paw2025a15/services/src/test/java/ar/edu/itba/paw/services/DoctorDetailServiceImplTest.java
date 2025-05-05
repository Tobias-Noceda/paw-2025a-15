package ar.edu.itba.paw.services;

import static org.mockito.Mockito.times;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;

@RunWith(MockitoJUnitRunner.class)
public class DoctorDetailServiceImplTest {

    private static final long DOC_ID = 1L;
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final DoctorDetail DOC_DETAIL = new DoctorDetail(DOC_ID, DOC_LICENCE, DOC_SPECIALTY);

    private static final long INSURANCE_ID = 1L;
    private static final long INSURANCE2_ID = 1L;
    private static final long INSURANCE_PIC_ID = 1L;
    private static final long INSURANCE_PIC2_ID = 2L;
    private static final String INSURANCE_NAME = "OSDE";
    private static final String INSURANCE_NAME2 = "Galeno";
    private static final Insurance INSURANCE = new Insurance(INSURANCE_ID, INSURANCE_NAME, INSURANCE_PIC_ID);
    private static final Insurance INSURANCE2 = new Insurance(INSURANCE2_ID, INSURANCE_NAME2, INSURANCE_PIC2_ID);
    private static final List<Long> INSURANCES = List.of(INSURANCE_ID, INSURANCE2_ID);
    private static final List<Insurance> INSURANCESLIST = List.of(INSURANCE, INSURANCE2);

    @InjectMocks
    private DoctorDetailServiceImpl dds;

    @Mock
    private DoctorDetailDao doctorDetailDaoMock;

    @Mock
    private InsuranceService is;

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

    @Test
    public void testCreateDoctorCoverages(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(Collections.emptyList());
        Mockito.when(is.getInsuranceById(Mockito.eq(INSURANCE_ID))).thenReturn(Optional.of(INSURANCE));
        Mockito.when(is.getInsuranceById(Mockito.eq(INSURANCE2_ID))).thenReturn(Optional.of(INSURANCE2));

        dds.createDoctorCoverages(DOC_ID, INSURANCES);

        Mockito.verify(doctorDetailDaoMock, times(INSURANCES.size())).addDoctorCoverage(Mockito.eq(DOC_ID), Mockito.anyLong());
    }

    @Test
    public void testCreateDoctorCoveragesNonexistentDoc(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dds.createDoctorCoverages(DOC_ID, INSURANCES)
        );

        Mockito.verify(doctorDetailDaoMock, Mockito.never()).addDoctorCoverage(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testCreateDoctorCoveragesExistentCoverages(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(INSURANCESLIST);

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dds.createDoctorCoverages(DOC_ID, INSURANCES)
        );
  
        Mockito.verify(doctorDetailDaoMock, Mockito.never()).addDoctorCoverage(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testCreateDoctorCoveragesNonexistentInsurance(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(Collections.emptyList());
        Mockito.when(is.getInsuranceById(Mockito.eq(INSURANCE_ID))).thenReturn(Optional.empty());
        Mockito.when(is.getInsuranceById(Mockito.eq(INSURANCE2_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dds.createDoctorCoverages(DOC_ID, INSURANCES)
        );
     
        Mockito.verify(doctorDetailDaoMock, Mockito.never()).addDoctorCoverage(Mockito.anyLong(), Mockito.anyLong());
    }
}
