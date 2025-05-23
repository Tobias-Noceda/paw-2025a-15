package ar.edu.itba.paw.services;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class InsuranceServiceImplTest {

    private static final long PIC_ID = 1L;
    private static final byte[] PIC_CONTENT = "Image".getBytes();
    private static final FileTypeEnum PIC_FILE_TYPE = FileTypeEnum.JPEG;
    private static final File PICTURE = new File(PIC_CONTENT, PIC_FILE_TYPE);

    private static final long PIC2_ID = 1L;
    private static final byte[] PIC_CONTENT2 = "Image".getBytes();
    private static final FileTypeEnum PIC_FILE_TYPE2 = FileTypeEnum.JPEG;
    private static final File PICTURE2 = new File(PIC_CONTENT2, PIC_FILE_TYPE2);

    private static final long INSURANCE_ID = 1L;
    private static final long INSURANCE2_ID = 1L;
    private static final long INSURANCE_PIC_ID = PIC_ID;
    private static final long INSURANCE_PIC2_ID = PIC2_ID;
    private static final String INSURANCE_NAME = "OSDE";
    private static final String INSURANCE_NAME2 = "Galeno";
    private static final Insurance INSURANCE = new Insurance(INSURANCE_NAME, PICTURE);
    private static final Insurance INSURANCE2 = new Insurance(INSURANCE_NAME2, PICTURE2);

    @InjectMocks
    private InsuranceServiceImpl is;

    @Mock
    private InsuranceDao insuranceDaoMock;

    @Mock
    private FileService fs;

    @Test
    public void testCreate(){
        Mockito.when(insuranceDaoMock.getInsuranceByName(Mockito.eq(INSURANCE_NAME))).thenReturn(Optional.empty());
        Mockito.when(fs.findById(Mockito.eq(PICTURE.getId()))).thenReturn(Optional.of(PICTURE));
        Mockito.when(insuranceDaoMock.create(Mockito.eq(INSURANCE_NAME), Mockito.eq(PICTURE))).thenReturn(INSURANCE);

        Insurance insurance = is.create(INSURANCE_NAME, PICTURE);

        Assert.assertNotNull(insurance);
        Assert.assertEquals(INSURANCE, insurance);
    }

    @Test
    public void testCreateExistentName(){
        Mockito.when(insuranceDaoMock.getInsuranceByName(Mockito.eq(INSURANCE_NAME))).thenReturn(Optional.of(INSURANCE));

        Assert.assertThrows(AlreadyExistsException.class, () -> 
            is.create(INSURANCE_NAME, PICTURE)
        );
    }

    @Test
    public void testCreateNonexistentPic(){
        Mockito.when(insuranceDaoMock.getInsuranceByName(Mockito.eq(INSURANCE_NAME))).thenReturn(Optional.empty());
        Mockito.when(fs.findById(PICTURE.getId())).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            is.create(INSURANCE_NAME, PICTURE)
        );
    }

    @Test
    public void testCreateFailure(){
        Mockito.when(insuranceDaoMock.getInsuranceByName(Mockito.eq(INSURANCE_NAME))).thenReturn(Optional.empty());
        Mockito.when(fs.findById(PICTURE.getId())).thenReturn(Optional.of(PICTURE));
        Mockito.when(insuranceDaoMock.create(Mockito.eq(INSURANCE_NAME), Mockito.eq(PICTURE))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            is.create(INSURANCE_NAME, PICTURE)
        );
    }

    @Test
    public void testEditNonexistentInsurance(){
        Mockito.when(insuranceDaoMock.getInsuranceById(Mockito.eq(INSURANCE_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            is.edit(INSURANCE_ID, INSURANCE_NAME2, PICTURE2)
        );
    }

    @Test
    public void testEditExistentName(){
        Mockito.when(insuranceDaoMock.getInsuranceById(Mockito.eq(INSURANCE_ID))).thenReturn(Optional.of(INSURANCE));
        Mockito.when(insuranceDaoMock.getInsuranceByName(Mockito.eq(INSURANCE_NAME2))).thenReturn(Optional.of(INSURANCE2));

        Assert.assertThrows(AlreadyExistsException.class, () -> 
            is.edit(INSURANCE_ID, INSURANCE_NAME2, PICTURE2)
        );
    }

    @Test
    public void testEditNonexistentPic(){
        Mockito.when(insuranceDaoMock.getInsuranceById(Mockito.eq(INSURANCE_ID))).thenReturn(Optional.of(INSURANCE));
        Mockito.when(insuranceDaoMock.getInsuranceByName(Mockito.eq(INSURANCE_NAME2))).thenReturn(Optional.empty());
        Mockito.when(fs.findById(Mockito.eq(PICTURE2.getId()))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            is.edit(INSURANCE_ID, INSURANCE_NAME2, PICTURE2)
        );
    }

    @Test
    public void testGetInsurancePicNonexistentInsurance(){
        Mockito.when(insuranceDaoMock.getInsuranceById(Mockito.eq(INSURANCE_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            is.getInsurancePicture(INSURANCE_ID)
        );
    }
}
