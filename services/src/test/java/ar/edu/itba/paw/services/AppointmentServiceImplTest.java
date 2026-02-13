package ar.edu.itba.paw.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.AppointmentAlreadyTakenException;
import ar.edu.itba.paw.models.exceptions.BadRequestException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceImplTest {

    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_CONTENT, FILETYPE);

    private static final long PATIENT_ID = 1L;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final LocalDate PATIENT_BIRTHDATE = LocalDate.parse("2001-01-01");
    private static final BigDecimal PATIENT_HEIGHT = BigDecimal.valueOf(1.75);
    private static final BigDecimal PATIENT_WEIGHT = BigDecimal.valueOf(89.00);
    private static final Patient PATIENT = new Patient(PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, FILE, PATIENT_CREATE_DATE, PATIENT_LOCALE, PATIENT_BIRTHDATE, PATIENT_HEIGHT, PATIENT_WEIGHT);

    private static final long DOC_ID = 2L;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final String DOC_LICENCE= "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY= SpecialtyEnum.CARDIOLOGY;
    private static final List<Insurance> DOC_INSURANCES = new ArrayList<>();
    private static final Doctor DOC = new Doctor(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, FILE, DOC_CREATE_DATE, DOC_LOCALE, DOC_LICENCE, DOC_SPECIALTY, DOC_INSURANCES);
 
    private static final long SHIFT_ID = 1L;
    private static final String ADDRESS = "fake123";
    private static final LocalTime START_TIME = LocalTime.parse("10:00:00");
    private static final LocalTime END_TIME = LocalTime.parse("10:30:00");
    private static final int DURATION = 30; // in minutes
    private static final LocalDate APP_DATE = LocalDate.now().plusDays(1);
    private static final String APP_DETAIL = "Appointment detail 1";
    private static final WeekdayEnum WEEKDAY = WeekdayEnum.fromInt(APP_DATE.getDayOfWeek().ordinal());
    private static final WeekdayEnum WRONG_WEEKDAY = WeekdayEnum.fromInt(APP_DATE.plusDays(1).getDayOfWeek().ordinal());
    private static final DoctorSingleShift SHIFT = new DoctorSingleShift(DOC, WEEKDAY, ADDRESS, START_TIME, END_TIME, DURATION);
    private static final DoctorSingleShift SHIFT_00 = new DoctorSingleShift(DOC, WEEKDAY, ADDRESS, LocalTime.parse("00:00:00"), END_TIME, DURATION);
    private static final DoctorSingleShift SHIFT_WRONG_WEEKDAY = new DoctorSingleShift(DOC, WRONG_WEEKDAY, ADDRESS, START_TIME, END_TIME, DURATION);
    
    private AppointmentNew APP;// = new AppointmentNew(SHIFT, PATIENT, APP_DATE, START_TIME, END_TIME);

    @InjectMocks
    private AppointmentServiceImpl as;

    @Mock
    private AppointmentDao appointmentDaoMock;

    @Mock
    private PatientService ps;

    @Mock
    private DoctorService ds;

    @Mock
    private DoctorShiftService dss;

    @Mock
    private EmailService es;

    @Mock 
    private AuthDoctorService ads;

    @Before
    public void setup() {
        SHIFT.setId(SHIFT_ID); // Ensure ID is set before creating APP
        APP = new AppointmentNew(SHIFT, PATIENT, APP_DATE, START_TIME, END_TIME, "Appointment detail 1");
    }
    
    @Test
    public void testAddAppointmentNonexistentPatient(){
        SHIFT.getDoctor().setId(DOC_ID);;
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentNonexistentShift(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentNonexistentDoc(){
        SHIFT.getDoctor().setId(DOC_ID);;
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentNonexistentDateIsNull(){
        DOC.setId(DOC_ID);
        SHIFT.setId(SHIFT_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, null, START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentNonexistentDateIsBeforeNowDay(){
        DOC.setId(DOC_ID);
        SHIFT.setId(SHIFT_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, LocalDate.now().minusDays(1), START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentNonexistentDateIsBeforeNowTime(){
        DOC.setId(DOC_ID);
        SHIFT.setId(SHIFT_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT_00));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, LocalDate.now(), START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentNonexistentDateWrongWeekday(){
        DOC.setId(DOC_ID);
        SHIFT.setId(SHIFT_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT_WRONG_WEEKDAY));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentTakenApp(){
        DOC.setId(DOC_ID);
        SHIFT.setId(SHIFT_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(appointmentDaoMock.getAppointmentByShiftDateAndTime(Mockito.eq(SHIFT), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME))).thenReturn(Optional.of(APP));

        Assert.assertThrows(AppointmentAlreadyTakenException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentCreateFailure(){
        DOC.setId(DOC_ID);
        SHIFT.setId(SHIFT_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(appointmentDaoMock.getAppointmentByShiftDateAndTime(Mockito.eq(SHIFT), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME))).thenReturn(Optional.empty());
        Mockito.when(appointmentDaoMock.addAppointment(Mockito.eq(SHIFT_ID), Mockito.eq(PATIENT_ID), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME), Mockito.eq(APP_DETAIL))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, APP_DETAIL)
        );
    }

    @Test
    public void testAddAppointmentCreate(){
        DOC.setId(DOC_ID);
        SHIFT.setId(SHIFT_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(appointmentDaoMock.getAppointmentByShiftDateAndTime(Mockito.eq(SHIFT), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME))).thenReturn(Optional.empty());
        Mockito.when(appointmentDaoMock.addAppointment(Mockito.eq(SHIFT_ID), Mockito.eq(PATIENT_ID), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME), Mockito.eq(APP_DETAIL))).thenReturn(APP);

        AppointmentNew appointment = as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, APP_DETAIL);

        Assert.assertNotNull(appointment);
        Assert.assertEquals(APP, appointment);
    }

    @Test
    public void testCancelAppointmentNonexistentApp(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(appointmentDaoMock.getAppointmentByShiftDateAndTime(Mockito.eq(SHIFT), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME))).thenReturn(Optional.empty());

        Assert.assertThrows(BadRequestException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, START_TIME, END_TIME, PATIENT_ID)
        );
    }

    @Test
    public void testCancelAppointmentNonexistentPatient(){
        DOC.setId(DOC_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(appointmentDaoMock.getAppointmentByShiftDateAndTime(Mockito.eq(SHIFT), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME))).thenReturn(Optional.of(APP));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, START_TIME, END_TIME, PATIENT_ID)
        );
    }

    @Test
    public void testCancelAppointmentNonexistentShift(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, START_TIME, END_TIME, PATIENT_ID)
        );
    }

    @Test
    public void testCancelAppointmentNonexistentDoc(){
        DOC.setId(DOC_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(appointmentDaoMock.getAppointmentByShiftDateAndTime(Mockito.eq(SHIFT), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME))).thenReturn(Optional.of(APP));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, START_TIME, END_TIME, PATIENT_ID)
        );
    }

    @Test
    public void testCancelAppointmentUnauthUser(){
        DOC.setId(DOC_ID);
        PATIENT.setId(PATIENT_ID);
        Mockito.when(appointmentDaoMock.getAppointmentByShiftDateAndTime(Mockito.eq(SHIFT), Mockito.eq(APP_DATE), Mockito.eq(START_TIME), Mockito.eq(END_TIME))).thenReturn(Optional.of(APP));
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(UnauthorizedException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, START_TIME, END_TIME, 100L)
        );
    }

    @Test
    public void testRemoveAppointmentNonexistentShift(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.removeAppointment(SHIFT_ID, APP_DATE, DOC_ID, START_TIME, END_TIME)
        );
    }

    @Test
    public void testRemoveAppointmentUnauthDoc(){
        DOC.setId(DOC_ID);
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));

        Assert.assertThrows(UnauthorizedException.class, () -> 
            as.removeAppointment(SHIFT_ID, APP_DATE, 100L, START_TIME, END_TIME)
        );
    }

    @Test
    public void testGetAppointmentByShiftIdDateAndTimeNonexistentShift(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.getAppointmentByShiftIdDateAndTime(SHIFT_ID, APP_DATE, START_TIME, END_TIME)
        );
    }

    @Test
    public void testGetOldAppointmentDataByPatientIdNonexistentPatient(){
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.getOldAppointmentDataByPatientId(PATIENT_ID)
        );
    }

    @Test
    public void testGetOldAppointmentDataPageByPatientIdNonexistentPatient(){
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.getOldAppointmentDataPageByPatientId(PATIENT_ID, 1, 100)
        );
    }

    @Test
    public void testGetOldAppointmentTotalByPatientIdNonexistentPatient(){
        Mockito.when(ps.getPatientById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.getOldAppointmentTotalByPatientId(PATIENT_ID)
        );
    }

    @Test
    public void testGetFutureAppointmentDataByDoctorIdNonexistentDoc(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.getFutureAppointmentDataByDoctorId(DOC_ID)
        );
    }

    @Test
    public void testGetFutureAppointmentDataPageByUserIdNonexistentDocAndPAt(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());
        Mockito.when(ps.getPatientById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.getFutureAppointmentDataPageByUserId(DOC_ID, 1, 100)
        );
    }

    @Test
    public void testGetFutureAppointmentTotalByUserIdNonexistentDocAndPat(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());
        Mockito.when(ps.getPatientById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.getFutureAppointmentTotalByUserId(DOC_ID)
        );
    }

    @Test
    public void testGetAvailableTurnsByDoctorIdByDateNonexistentDoc(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            as.getAvailableTurnsByDoctorIdByDate(DOC_ID, LocalDate.now())
        );
    }

}