package ar.edu.itba.paw.services;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    
    private static final long PATIENT_ID = 1L;
    
    private static final String DOC_PASSWORD = "shortandsweet";
    
    @InjectMocks
    private UserServiceImpl us;

    @Mock
    private UserDao userDaoMock;

    @Test
    public void testChangePasswordByIDNonexistentUser(){
        Mockito.when(userDaoMock.getUserById(Mockito.eq(PATIENT_ID))).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> 
            us.changePasswordByID(PATIENT_ID, DOC_PASSWORD)
        );
    }

}