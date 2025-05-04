package ar.edu.itba.paw.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.DoctorCoverageDao;

@RunWith(MockitoJUnitRunner.class)
public class DoctorCoverageServiceImplTest {

    @InjectMocks
    private DoctorCoverageServiceImpl dcs;

    @Mock
    private DoctorCoverageDao doctorCoverageDaoMock;

    @Test
    public void test(){

    }

}
