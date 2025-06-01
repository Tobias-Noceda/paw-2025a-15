package ar.edu.itba.paw.services;

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

import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
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
    private DoctorDetailService dds;

    @Test
    public void testCreateShiftsNonexistentDoc(){
        Mockito.when(dds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, SLOT)
        );
    }

    @Test
    public void testCreateShiftsWrongTimes(){
        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, END_TIME, START_TIME, SLOT)
        );
    }

    // TODO: ver, pero no creo q tenga sentido con los nuevos shifts
    // @Test
    // public void testCreateShiftsBatchFailure(){
    //     Mockito.when(dds.getDoctorById(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC));
    //     Mockito.when(doctorShiftDaoMock.batchCreate(Mockito.eq(SHIFT))).thenReturn(new int[]{1, 0});

    //     Assert.assertThrows(RuntimeException.class, () -> 
    //         dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, slot)
    //     );
    // }

}
