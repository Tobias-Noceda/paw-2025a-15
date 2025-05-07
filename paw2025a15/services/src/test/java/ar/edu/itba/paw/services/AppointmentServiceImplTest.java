package ar.edu.itba.paw.services;

import static org.mockito.Answers.valueOf;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceImplTest {

    private static final long PATIENT_ID = 1L;
    private static final String PATIENT_EMAIL = "grace@example.com";
    private static final String PATIENT_NAME = "grace";
    private static final String PATIENT_NAME2 = "gracie";
    private static final String PATIENT_PASSWORD = "goodgraces";
    private static final String PATIENT_TELEPHONE = "1144445555";
    private static final String PATIENT_TELEPHONE2 = "1144446666";
    private static final UserRoleEnum PATIENT_ROLE = UserRoleEnum.PATIENT;
    private static final LocaleEnum PATIENT_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate PATIENT_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User PATIENT = new User(PATIENT_ID, PATIENT_EMAIL, PATIENT_PASSWORD, PATIENT_NAME, PATIENT_TELEPHONE, PATIENT_ROLE, PATIENT_CREATE_DATE, PATIENT_LOCALE);

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

    private static final long SHIFT_ID = 1L;
    private static final String ADDRESS = "fake123";
    private static final LocalTime START_TIME = LocalTime.parse("10:00:00");
    private static final LocalTime END_TIME = LocalTime.parse("10:30:00");
    private static final LocalDate APP_DATE = LocalDate.now().plusDays(1);
    private static final WeekdayEnum WEEKDAY = WeekdayEnum.fromInt(APP_DATE.getDayOfWeek().ordinal());
    private static final WeekdayEnum WRONG_WEEKDAY = WeekdayEnum.fromInt(APP_DATE.plusDays(1).getDayOfWeek().ordinal());
    private static final DoctorShift SHIFT = new DoctorShift(SHIFT_ID, DOC_ID, WEEKDAY, ADDRESS, START_TIME, END_TIME);
    private static final DoctorShift SHIFT_00 = new DoctorShift(SHIFT_ID, DOC_ID, WEEKDAY, ADDRESS, LocalTime.parse("00:00:00"), END_TIME);
    private static final DoctorShift SHIFT_WRONG_WEEKDAY = new DoctorShift(SHIFT_ID, DOC_ID, WRONG_WEEKDAY, ADDRESS, LocalTime.parse("00:00:00"), END_TIME);
    
    private static final Appointment APP = new Appointment(SHIFT_ID, PATIENT_ID, APP_DATE);

    @InjectMocks
    private AppointmentServiceImpl as;

    @Mock
    private AppointmentDao appointmentDaoMock;

    @Mock
    private UserService us;

    @Mock
    private DoctorShiftService dss;

    @Test
    public void testAddAppointmentNonexistentPatient(){
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE)
        );
    }

    @Test
    public void testAddAppointmentNonexistentShift(){
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE)
        );
    }

    @Test
    public void testAddAppointmentNonexistentDoc(){
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE)
        );
    }

    @Test
    public void testAddAppointmentNonexistentDateIsNull(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, null)
        );
    }

    @Test
    public void testAddAppointmentNonexistentDateIsBeforeNowDay(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, LocalDate.now().minusDays(1))
        );
    }

    @Test
    public void testAddAppointmentNonexistentDateIsBeforeNowTime(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT_00));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, LocalDate.now())
        );
    }

    @Test
    public void testAddAppointmentNonexistentDateWrongWeekday(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT_WRONG_WEEKDAY));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE)
        );
    }

    @Test
    public void testAddAppointmentTakenApp(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(appointmentDaoMock.getAppointmentsByShiftIdAndDate(Mockito.eq(SHIFT_ID), Mockito.eq(APP_DATE))).thenReturn(Optional.of(APP));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE)
        );
    }

    @Test
    public void testAddAppointmentCreateFailure(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(appointmentDaoMock.getAppointmentsByShiftIdAndDate(Mockito.eq(SHIFT_ID), Mockito.eq(APP_DATE))).thenReturn(Optional.empty());
        Mockito.when(appointmentDaoMock.addAppointment(Mockito.eq(SHIFT_ID), Mockito.eq(PATIENT_ID), Mockito.eq(APP_DATE))).thenReturn(null);

        Assert.assertThrows(RuntimeException.class, () -> 
            as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE)
        );
    }

    @Test
    public void testAddAppointmentCreate(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(appointmentDaoMock.getAppointmentsByShiftIdAndDate(Mockito.eq(SHIFT_ID), Mockito.eq(APP_DATE))).thenReturn(Optional.empty());
        Mockito.when(appointmentDaoMock.addAppointment(Mockito.eq(SHIFT_ID), Mockito.eq(PATIENT_ID), Mockito.eq(APP_DATE))).thenReturn(APP);

        Appointment appointment = as.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE);

        Assert.assertNotNull(appointment);
        Assert.assertEquals(APP, appointment);
    }

    @Test
    public void testCancelAppointmentNonexistentApp(){
        Mockito.when(appointmentDaoMock.getAppointmentsByShiftIdAndDate(Mockito.eq(SHIFT_ID), Mockito.eq(APP_DATE))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, PATIENT_ID)
        );
    }

    @Test
    public void testCancelAppointmentNonexistentPatient(){
        Mockito.when(appointmentDaoMock.getAppointmentsByShiftIdAndDate(Mockito.eq(SHIFT_ID), Mockito.eq(APP_DATE))).thenReturn(Optional.of(APP));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, PATIENT_ID)
        ).getMessage().contains("Patient");
    }

    @Test
    public void testCancelAppointmentNonexistentShift(){
        Mockito.when(appointmentDaoMock.getAppointmentsByShiftIdAndDate(Mockito.eq(SHIFT_ID), Mockito.eq(APP_DATE))).thenReturn(Optional.of(APP));
        Mockito.when(us.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.of(PATIENT));
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, PATIENT_ID)
        ).getMessage().contains("Shift not found");
    }

    @Test
    public void testCancelAppointmentNonexistentDoc(){
        Mockito.when(appointmentDaoMock.getAppointmentsByShiftIdAndDate(Mockito.eq(SHIFT_ID), Mockito.eq(APP_DATE))).thenReturn(Optional.of(APP));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, PATIENT_ID)
        ).getMessage().contains("Doctor");
    }

    @Test
    public void testCancelAppointmentUnauthUser(){
        Mockito.when(appointmentDaoMock.getAppointmentsByShiftIdAndDate(Mockito.eq(SHIFT_ID), Mockito.eq(APP_DATE))).thenReturn(Optional.of(APP));
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.cancelAppointment(SHIFT_ID, APP_DATE, 100L)
        );
    }

    @Test
    public void testRemoveAppointmentNonexistentShift(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.removeAppointment(SHIFT_ID, APP_DATE, DOC_ID)
        );
    }

    @Test
    public void testRemoveAppointmentUnauthDoc(){
        Mockito.when(dss.getShiftById(Mockito.eq(SHIFT_ID))).thenReturn(Optional.of(SHIFT));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            as.removeAppointment(SHIFT_ID, APP_DATE, 100L)
        );
    }

}
