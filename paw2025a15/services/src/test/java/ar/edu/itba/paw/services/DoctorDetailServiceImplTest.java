package ar.edu.itba.paw.services;

import java.time.LocalDate;
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
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class DoctorDetailServiceImplTest {
    
    private static final long DOC_ID = 1L;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final UserRoleEnum DOC_ROLE = UserRoleEnum.DOCTOR;
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User DOC = new User(DOC_ID, DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_ROLE, DOC_CREATE_DATE, DOC_LOCALE);
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
    private UserService us;

    @Test
    public void testCreateDoctor(){
        Mockito.when(us.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(us.create(Mockito.eq(DOC_EMAIL), Mockito.eq(DOC_PASSWORD), Mockito.eq(DOC_NAME), Mockito.eq(DOC_TELEPHONE), Mockito.eq(DOC_ROLE), Mockito.eq(DOC_LOCALE))).thenReturn(DOC);
        Mockito.when(doctorDetailDaoMock.create(Mockito.eq(DOC_ID), Mockito.eq(DOC_LICENCE), Mockito.eq(DOC_SPECIALTY))).thenReturn(DOC_DETAIL);

        User user = dds.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, DOC_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(DOC, user);
    }

    @Test
    public void testCreateDoctorExistentUser(){
        Mockito.when(us.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(AlreadyExistsException.class, () -> 
            dds.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, DOC_LOCALE)
        );
    }

    @Test
    public void testCreateDoctorDDFailure(){
        Mockito.when(us.getUserByEmail(Mockito.eq(DOC_EMAIL))).thenReturn(Optional.empty());
        Mockito.when(us.create(Mockito.eq(DOC_EMAIL), Mockito.eq(DOC_PASSWORD), Mockito.eq(DOC_NAME), Mockito.eq(DOC_TELEPHONE), Mockito.eq(DOC_ROLE), Mockito.eq(DOC_LOCALE))).thenReturn(DOC);
        Mockito.when(doctorDetailDaoMock.create(Mockito.eq(DOC_ID), Mockito.eq(DOC_LICENCE), Mockito.eq(DOC_SPECIALTY))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
        dds.createDoctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_LICENCE, DOC_SPECIALTY, DOC_LOCALE)
        ).getMessage().contains("Failed to create doctor details for userId");
    }

    @Test
    public void testUpdateDoctorCoveragesNonexistentDoctor(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dds.updateDoctorCoverages(DOC_ID, INSURANCES)
        );
    }

    @Test
    public void testUpdateDoctorCoveragesDaoAddFailureWhenCurrentNull(){
        int[] badResults = {1,0};
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(Collections.emptyList());
        Mockito.when(doctorDetailDaoMock.addDoctorCoverages(Mockito.anyLong(), Mockito.anyList())).thenReturn(badResults);

        Assert.assertThrows(RuntimeException.class, () -> 
            dds.updateDoctorCoverages(DOC_ID, INSURANCES)
        );
    }

    @Test
    public void testUpdateDoctorCoveragesDaoAddFailureExistingCurrent(){
        int[] badResults = {1,0};
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(List.of(INSURANCE));
        Mockito.when(doctorDetailDaoMock.addDoctorCoverages(Mockito.anyLong(), Mockito.anyList())).thenReturn(badResults);

        Assert.assertThrows(RuntimeException.class, () -> 
            dds.updateDoctorCoverages(DOC_ID, INSURANCES)
        );
    }

    @Test
    public void testCreateDoctorCoveragesNonexistentDoc(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dds.createDoctorCoverages(DOC_ID, INSURANCES)
        );
    }

    @Test
    public void testCreateDoctorCoveragesExistentCoverages(){
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(INSURANCESLIST);

        Assert.assertThrows(AlreadyExistsException.class, () -> 
            dds.createDoctorCoverages(DOC_ID, INSURANCES)
        );
    }

    @Test
    public void testCreateDoctorCoveragesNonexistentInsurance(){
        int[] badResults = {1,0};
        Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(Collections.emptyList());
        Mockito.when(doctorDetailDaoMock.addDoctorCoverages(Mockito.anyLong(), Mockito.anyList())).thenReturn(badResults);

        Assert.assertThrows(RuntimeException.class, () -> 
            dds.createDoctorCoverages(DOC_ID, INSURANCES)
        );
    }
}
