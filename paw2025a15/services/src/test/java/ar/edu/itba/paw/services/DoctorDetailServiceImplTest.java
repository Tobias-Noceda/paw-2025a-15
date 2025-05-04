package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

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
