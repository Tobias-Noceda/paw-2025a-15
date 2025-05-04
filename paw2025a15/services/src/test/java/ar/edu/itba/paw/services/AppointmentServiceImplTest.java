package ar.edu.itba.paw.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceImplTest {

    @InjectMocks
    private AppointmentServiceImpl as;

    @Mock
    private AppointmentDao appointmentDaoMock;

    @Test
    public void test(){

    }

}
