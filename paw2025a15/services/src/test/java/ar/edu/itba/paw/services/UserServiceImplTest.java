package ar.edu.itba.paw.services;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long PIC_ID = 1L;
    private static final byte[] PIC_CONTENT = "Image".getBytes();
    private static final FileTypeEnum PIC_FILE_TYPE = FileTypeEnum.JPEG;
    private static final File PICTURE = new File(PIC_ID, PIC_CONTENT, PIC_FILE_TYPE);

    private static final long PATIENT_ID = 1L;
    private static final long PATIENT_PIC_ID = PIC_ID;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final UserRoleEnum PATIENT_ROLE = UserRoleEnum.PATIENT;
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User PATIENT = new User(PATIENT_ID, PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_CREATE_DATE, PATIENT_LOCALE);

    @InjectMocks
    private UserServiceImpl us;

    @Mock
    private UserDao userDaoMock;

    @Mock
    private PatientDetailService pds;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreatePatient(){
        Mockito.when(passwordEncoder.encode(Mockito.eq(PATIENT_PASSWORD))).thenReturn("userPassEnc");
        Mockito.when(userDaoMock.create(Mockito.eq(PATIENT_EMAIL), Mockito.eq("userPassEnc"), Mockito.eq(PATIENT_NAME), Mockito.eq(PATIENT_TELEPHONE), Mockito.eq(PATIENT_ROLE), Mockito.eq(PATIENT_PIC_ID), Mockito.eq(PATIENT_LOCALE))).thenReturn(PATIENT);

        User user = us.createPatient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(PATIENT, user);
        Mockito.verify(pds).create(PATIENT_ID, null, null, null, null, null, null, null, null, null, null, null, null);
    }

}
