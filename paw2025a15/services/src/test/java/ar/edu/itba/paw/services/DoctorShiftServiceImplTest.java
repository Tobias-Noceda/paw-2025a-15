package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class DoctorShiftServiceImplTest {
    private static final long DOC_ID = 1L;
    
    private static final String ADDRESS = "fake123";
    private static final LocalTime START_TIME = LocalTime.parse("10:00:00");
    private static final LocalTime END_TIME = LocalTime.parse("10:30:00");
    private static final int SLOT = 15;
    private static final List<WeekdayEnum> WEEKDAYS = List.of(WeekdayEnum.THURSDAY);
    
    @InjectMocks
    private DoctorShiftServiceImpl dss;

    @Mock
    private DoctorService ds;

    @Test
    public void testCreateShiftsNullStartTime(){
        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, null, END_TIME, SLOT)
        );
    }

    @Test
    public void testCreateShiftsNullEndTime(){
        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, null, SLOT)
        );
    }

    @Test
    public void testCreateShiftsWrongTimes(){
        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, END_TIME, START_TIME, SLOT)
        );
    }

    @Test
    public void testCreateShiftsNonexistentDoc(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, SLOT)
        );
    }

    @Test
    public void testGetAvailableTurnsByDOctorIdByDateNonexistentDoctor(){
        Mockito.when(ds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dss.getAvailableTurnsByDoctorIdByDate(DOC_ID, LocalDate.now())
        );
    }

}
