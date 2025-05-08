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
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@RunWith(MockitoJUnitRunner.class)
public class DoctorShiftServiceImplTest {

    private static final long DOC_ID = 1L;
    private static final String DOC_LICENCE = "med-licence";
    private static final SpecialtyEnum DOC_SPECIALTY = SpecialtyEnum.CARDIOLOGY;
    private static final DoctorDetail DOC_DETAIL = new DoctorDetail(DOC_ID, DOC_LICENCE, DOC_SPECIALTY);

    private static final String ADDRESS = "fake123";
    private static final LocalTime START_TIME = LocalTime.parse("10:00:00");
    private static final LocalTime END_TIME = LocalTime.parse("10:30:00");
    private static final int slot = 15;
    private static final List<WeekdayEnum> WEEKDAYS = List.of(WeekdayEnum.THURSDAY);
    private static final List<DoctorShift> SHIFTS = List.of(
        new DoctorShift(1, DOC_ID, WeekdayEnum.THURSDAY, ADDRESS, START_TIME, START_TIME.plusMinutes(slot)),
        new DoctorShift(2, DOC_ID, WeekdayEnum.THURSDAY, ADDRESS, START_TIME.plusMinutes(slot), END_TIME)
    );

    private static final DoctorShift SHIFT = new DoctorShift(1, DOC_ID, WeekdayEnum.MONDAY, ADDRESS, START_TIME, END_TIME);

    private static final int YEAR = LocalDate.now().getYear()+1;
    private static final Month MONTH = LocalDate.now().getMonth();
    private static final int WEEK0 = 0;
    private static final int WEEK1 = 1;
    private static final int WEEK2 = 2;
    private static final int WEEK3 = 3;

    private static final AvailableTurn AV_TURN = new AvailableTurn(LocalDate.of(YEAR, MONTH, 1), START_TIME, END_TIME, ADDRESS, DOC_ID);
    private static final AvailableTurn AV_TURN1 = new AvailableTurn(LocalDate.of(YEAR, MONTH, 8), START_TIME, END_TIME, ADDRESS, DOC_ID);
    private static final AvailableTurn AV_TURN2 = new AvailableTurn(LocalDate.of(YEAR, MONTH, 15), START_TIME, END_TIME, ADDRESS, DOC_ID);
    private static final AvailableTurn AV_TURN3 = new AvailableTurn(LocalDate.of(YEAR, MONTH, 23), START_TIME, END_TIME, ADDRESS, DOC_ID);
    private static final List<AvailableTurn> AV_TURNS = List.of(AV_TURN, AV_TURN1, AV_TURN2, AV_TURN3);

    @InjectMocks
    private DoctorShiftServiceImpl dss;

    @Mock
    private DoctorShiftDao doctorShiftDaoMock;

    @Mock
    private DoctorDetailService dds;

    @Test
    public void testCreateShiftsNonexistentDoc(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
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
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorShiftDaoMock.batchCreate(Mockito.eq(SHIFTS))).thenReturn(new int[]{1, 0});

        Assert.assertThrows(RuntimeException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, slot)
        );
    }

    @Test
    public void testGetUnifiedShiftsByDoctorIdNonexistentDOc(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
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

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.getAvailableTurnsByDoctorIdByMonthAndWeekNumber(DOC_ID, MONTH, WEEK0)
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
