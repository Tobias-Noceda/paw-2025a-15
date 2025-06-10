package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.DoctorDao;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class DoctorServiceImplTest {

    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_CONTENT, FILETYPE);
    
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
    private static final List<Long> INSURANCES = List.of(INSURANCE_ID, INSURANCE2_ID);

    @InjectMocks
    private DoctorServiceImpl ds;

    @Mock
    private DoctorDao doctorDaoMock;


    @Test
    public void testUpdateDoctorCoveragesNonexistentDoctor(){
        DOC.setId(DOC_ID);
        Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ds.updateDoctor(DOC, DOC_TELEPHONE, FILE, DOC_LOCALE, INSURANCES)
        );
    }    

    @Test
    public void testSetDoctorCoveragesNonexistentDoc(){
        DOC.setId(DOC_ID);
        Mockito.when(doctorDaoMock.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            ds.updateDoctor(DOC, DOC_TELEPHONE, FILE, DOC_LOCALE, INSURANCES)
        );
    }

}