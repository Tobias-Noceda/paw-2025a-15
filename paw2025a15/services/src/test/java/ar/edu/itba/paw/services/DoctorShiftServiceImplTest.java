package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
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

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.DoctorDetail;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class DoctorShiftServiceImplTest {

    private static final byte[] FILE_CONTENT = "Image".getBytes();
    private static final FileTypeEnum FILETYPE = FileTypeEnum.JPEG;
    private static final File FILE = new File(FILE_CONTENT, FILETYPE);

    private static final long DOC_ID = 1L;
    private static final String DOC_EMAIL = "sabrina@example.com";
    private static final String DOC_NAME = "sabrina";
    private static final String DOC_PASSWORD = "shortandsweet";
    private static final String DOC_TELEPHONE = "1144445555";
    private static final UserRoleEnum DOC_ROLE = UserRoleEnum.DOCTOR;
    private static final LocaleEnum DOC_LOCALE = LocaleEnum.ES_AR;
    private static final LocalDate DOC_CREATE_DATE = LocalDate.parse("2025-04-09");
    private static final User DOC = new User(DOC_EMAIL, DOC_PASSWORD, DOC_NAME, DOC_TELEPHONE, DOC_ROLE, FILE, DOC_CREATE_DATE, DOC_LOCALE);
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final DoctorDetail DOC_DETAIL = new DoctorDetail(DOC, DOC_LICENCE, DOC_SPECIALTY);

    private static final String ADDRESS = "fake123";
    private static final LocalTime START_TIME = LocalTime.parse("10:00:00");
    private static final LocalTime END_TIME = LocalTime.parse("10:30:00");
    private static final int slot = 15;
    private static final List<WeekdayEnum> WEEKDAYS = List.of(WeekdayEnum.THURSDAY);
    private static final List<DoctorShift> SHIFTS = List.of(
        new DoctorShift(DOC, WeekdayEnum.THURSDAY, ADDRESS, START_TIME, START_TIME.plusMinutes(slot)),
        new DoctorShift(DOC, WeekdayEnum.THURSDAY, ADDRESS, START_TIME.plusMinutes(slot), END_TIME)
    );

    private static final DoctorShift SHIFT = new DoctorShift(DOC, WeekdayEnum.MONDAY, ADDRESS, START_TIME, END_TIME);

    private static final Month MONTH = LocalDate.now().getMonth();

    @InjectMocks
    private DoctorShiftServiceImpl dss;

    @Mock
    private DoctorShiftDao doctorShiftDaoMock;

    @Mock
    private DoctorDetailService dds;

    @Mock
    private UserService us;

    @Test
    public void testCreateShiftsNonexistentDoc(){
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, slot)
        );
    }

    @Test
    public void testCreateShiftsWrongTimes(){
        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, END_TIME, START_TIME, slot)
        );
    }

    @Test
    public void testCreateShiftsBatchFailure(){
        Mockito.when(us.getUserById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorShiftDaoMock.batchCreate(Mockito.eq(SHIFTS))).thenReturn(new int[]{1, 0});

        Assert.assertThrows(RuntimeException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, slot)
        );
    }

    @Test
    public void testGetUnifiedShiftsByDoctorIdNonexistentDOc(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dss.getUnifiedShiftsByDoctorId(DOC_ID)
        );
    }

    @Test
    public void testGetUnifiedShiftsByDoctorIdShiftsAreNull(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorShiftDaoMock.getShiftsByDoctorId(Mockito.eq(DOC_ID))).thenReturn(null);
        
        List<DoctorShift> results = dss.getUnifiedShiftsByDoctorId(DOC_ID);
        
        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testGetUnifiedShiftsByDoctorIdShiftsAreEmpty(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorShiftDaoMock.getShiftsByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Collections.emptyList());
        
        List<DoctorShift> results = dss.getUnifiedShiftsByDoctorId(DOC_ID);
        
        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testGetUnifiedShiftsByDoctorIdShiftsOnlyOne(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorShiftDaoMock.getShiftsByDoctorId(Mockito.eq(DOC_ID))).thenReturn(List.of(SHIFT));
        
        List<DoctorShift> results = dss.getUnifiedShiftsByDoctorId(DOC_ID);
        
        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(SHIFT, results.get(0));
    }

    @Test
    public void testGetAvailableTurnsByDoctorIdByMonthAndWeekNumberNonexistentDoc(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dss.getAvailableTurnsByDoctorIdByMonthAndWeekNumber(DOC_ID, MONTH, 0)
        );
    }

    @Test
    public void testGetAvailableTurnsByDoctorIdByMonthAndWeekNumberNonexistentWeekNumber(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.getAvailableTurnsByDoctorIdByMonthAndWeekNumber(DOC_ID, MONTH, 100)
        );
    }

}
