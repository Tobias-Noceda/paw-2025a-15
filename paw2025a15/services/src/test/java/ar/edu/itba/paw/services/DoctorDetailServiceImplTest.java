package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.ArrayList;
// import java.util.Collections;
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
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
// import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class DoctorDetailServiceImplTest {

    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_CONTENT, FILETYPE);

    private static final byte[] FILE_CONTENT2 = "Image2".getBytes();
    private static final File FILE2 = new File(FILE_CONTENT2, FILETYPE);
    
    private static final long DOC_ID = 1L;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final List<Insurance> DOC_INSURANCES = new ArrayList<>();
    private static final Doctor DOC = new Doctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, FILE, DOC_CREATE_DATE, DOC_LOCALE, DOC_LICENCE, DOC_SPECIALTY, DOC_INSURANCES);

    private static final long INSURANCE_ID = 1L;
    private static final long INSURANCE2_ID = 1L;
    private static final String INSURANCE_NAME = "OSDE";
    private static final String INSURANCE_NAME2 = "Galeno";
    private static final Insurance INSURANCE = new Insurance(INSURANCE_NAME, FILE);
    private static final Insurance INSURANCE2 = new Insurance(INSURANCE_NAME2, FILE2);
    private static final List<Long> INSURANCES = List.of(INSURANCE_ID, INSURANCE2_ID);
    private static final List<Insurance> INSURANCESLIST = List.of(INSURANCE, INSURANCE2);

    @InjectMocks
    private DoctorDetailServiceImpl dds;

    @Mock
    private DoctorDetailDao doctorDetailDaoMock;


    @Test
    public void testUpdateDoctorCoveragesNonexistentDoctor(){
        Mockito.when(doctorDetailDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dds.updateDoctor(DOC, DOC_NAME, FILE, DOC_LOCALE, INSURANCES)
        );
    }

    // TODO: ver si se puede hacer de otra forma
    // @Test
    // public void testUpdateDoctorCoveragesDaoAddFailureWhenCurrentNull(){
    //     int[] badResults = {1,0};
    //     Mockito.when(doctorDetailDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(doctorDetailDaoMock.updateDoctor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(File.class), Mockito.any(LocaleEnum.class), Mockito.anyList())).thenReturn(badResults);

    //     Assert.assertThrows(RuntimeException.class, () -> 
    //         dds.updateDoctor(DOC, DOC_TELEPHONE, FILE, DOC_LOCALE, INSURANCES)
    //     );
    // }

    // TODO: ver si se puede hacer de otra forma
    // @Test
    // public void testUpdateDoctorCoveragesDaoAddFailureExistingCurrent(){
    //     int[] badResults = {1,0};
    //     Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
    //     Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(List.of(INSURANCE));
    //     Mockito.when(doctorDetailDaoMock.addDoctorCoverages(Mockito.anyLong(), Mockito.anyList())).thenReturn(badResults);

    //     Assert.assertThrows(RuntimeException.class, () -> 
    //         dds.updateDoctorCoverages(DOC_ID, INSURANCES)
    //     );
    // }

    @Test
    public void testSetDoctorCoveragesNonexistentDoc(){
        Mockito.when(doctorDetailDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dds.updateDoctor(DOC, DOC_NAME, FILE, DOC_LOCALE, INSURANCES)
        );
    }

    // TODO: ver q onda esta, para mi con hibernate ya no tiene sentido
    // @Test
    // public void testCreateDoctorCoveragesExistentCoverages(){
    //     Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
    //     Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(INSURANCESLIST);

    //     Assert.assertThrows(AlreadyExistsException.class, () -> 
    //         dds.createDoctorCoverages(DOC_ID, INSURANCES)
    //     );
    // }

    // TODO: ver q onda esta, para mi con hibernate ya no tiene sentido
    // @Test
    // public void testCreateDoctorCoveragesNonexistentInsurance(){
    //     int[] badResults = {1,0};
    //     Mockito.when(doctorDetailDaoMock.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
    //     Mockito.when(doctorDetailDaoMock.getDoctorInsurancesById(Mockito.eq(DOC_ID))).thenReturn(Collections.emptyList());
    //     Mockito.when(doctorDetailDaoMock.addDoctorCoverages(Mockito.anyLong(), Mockito.anyList())).thenReturn(badResults);

    //     Assert.assertThrows(RuntimeException.class, () -> 
    //         dds.createDoctorCoverages(DOC_ID, INSURANCES)
    //     );
    // }
}