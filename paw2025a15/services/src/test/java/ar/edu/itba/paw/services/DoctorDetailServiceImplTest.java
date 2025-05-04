package ar.edu.itba.paw.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;

@RunWith(MockitoJUnitRunner.class)
public class DoctorDetailServiceImplTest {

    @InjectMocks
    private DoctorDetailServiceImpl dds;

    @Mock
    private DoctorDetailDao doctorDetailDaoMock;

    @Test
    public void test(){

    }
}
