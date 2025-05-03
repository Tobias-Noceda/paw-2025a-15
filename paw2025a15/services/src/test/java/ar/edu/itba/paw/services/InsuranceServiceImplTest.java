package ar.edu.itba.paw.services;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;

@RunWith(MockitoJUnitRunner.class)
public class InsuranceServiceImplTest {

    @InjectMocks
    private InsuranceServiceImpl is;

    @Mock
    private InsuranceDao insuranceDaoMock;

    @Test
}
