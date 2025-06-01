package ar.edu.itba.paw.services;

import java.math.BigDecimal;
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
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class PatientDetailServiceImplTest {

    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_CONTENT, FILETYPE);

    private static final long PATIENT_ID = 1L;
    
    private static final String PAT_EMAIL = "sabrina@example.com";
    private static final String PAT_NAME = "sabrina";
    private static final String PAT_PASSWORD = "shortandsweet";
    private static final String PAT_TELEPHONE = "1144445555";
    private static final LocaleEnum PAT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PAT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final LocalDate BIRTHDATE = LocalDate.parse("2000-01-01");
    private static final BloodTypeEnum BLOODTYPE = BloodTypeEnum.AB_POSITIVE;
    private static final BigDecimal HEIGHT = BigDecimal.valueOf(1.50);
    private static final BigDecimal WEIGHT = BigDecimal.valueOf(56.00);
    private static final Boolean SMOKES = false;
    private static final Boolean DRINKS = true;
    private static final String MEDS = "none";
    private static final String CONDITIONS = "none";
    private static final String ALLERGIES = "none";
    private static final String DIET = "food";
    private static final String HOBBIES = "sing";
    private static final String JOB = "carpenter";
    private static final Patient PATIENT = new Patient(PAT_EMAIL, PAT_PASSWORD, PAT_NAME, PAT_TELEPHONE, FILE, PAT_CREATE_DATE, PAT_LOCALE, BIRTHDATE, HEIGHT, WEIGHT);

    @InjectMocks
    private PatientDetailServiceImpl pds;

    @Mock
    private PatientDetailDao patientDetailDaoMock;

    @Mock
    private UserService us;


    @Test
    public void testUpdatePatientDetailsNonexistent(){
        Mockito.when(patientDetailDaoMock.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            pds.updatePatient(PATIENT, PAT_TELEPHONE, FILE, PAT_LOCALE, BIRTHDATE, BLOODTYPE, HEIGHT, WEIGHT, SMOKES, DRINKS, MEDS, CONDITIONS, ALLERGIES, DIET, HOBBIES, JOB)
        );
    }

}
