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

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
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
    private static final int[] BATCHRESULTS = new int[]{1, 1};

    @InjectMocks
    private DoctorShiftServiceImpl dss;

    @Mock
    private DoctorShiftDao doctorShiftDaoMock;

    @Mock
    private DoctorDetailService dds;

    @Test
    public void testCreateShifts(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorShiftDaoMock.batchCreate(Mockito.eq(SHIFTS))).thenReturn(BATCHRESULTS);

        dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, slot);
        
        Mockito.verify(doctorShiftDaoMock).batchCreate(SHIFTS);
    }

    @Test
    public void testCreateShiftsNonexistentDoc(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, slot)
        );
        
        Mockito.verify(doctorShiftDaoMock, Mockito.never()).batchCreate(Mockito.any());
    }

    @Test
    public void testCreateShiftsWrongTimes(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));

        Assert.assertThrows(IllegalArgumentException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, END_TIME, START_TIME, slot)
        );
        
        Mockito.verify(doctorShiftDaoMock, Mockito.never()).batchCreate(Mockito.any());
    }

    @Test
    public void testCreateShiftsBatchFailure(){
        Mockito.when(dds.getDetailByDoctorId(Mockito.eq(DOC_ID))).thenReturn(Optional.of(DOC_DETAIL));
        Mockito.when(doctorShiftDaoMock.batchCreate(Mockito.eq(SHIFTS))).thenReturn(new int[]{1, 0});

        Assert.assertThrows(RuntimeException.class, () -> 
            dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, slot)
        );
        
        Mockito.verify(doctorShiftDaoMock).batchCreate(Mockito.eq(SHIFTS));
    }

}
