package ar.edu.itba.paw.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@RunWith(MockitoJUnitRunner.class)
public class DoctorShiftServiceImplTest {

    @InjectMocks
    private DoctorShiftServiceImpl dss;

    @Mock
    private DoctorShiftDao doctorShiftDaoMock;

    @Test
    public void testCreateShifts(){
       // Mockito.when();

        //dss.createShifts(DOC_ID, WEEKDAYS, ADDRESS, START_TIME, END_TIME, SLOT);

       /// Mockito.verify(doctorShiftDaoMock).create(Mockito.eq(DOC_ID), Mockito.eq(WEEKDAY), Mockito.eq(ADDRESS), Mockito.eq(START_TIME), Mockito.eq(END_TIME));
    }

}
