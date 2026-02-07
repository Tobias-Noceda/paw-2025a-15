// package ar.edu.itba.paw.services;

// import java.util.Optional;

// import org.junit.Assert;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.MockitoJUnitRunner;

// import ar.edu.itba.paw.interfaces.persistence.UserDao;
// import ar.edu.itba.paw.models.exceptions.NotFoundException;

// @RunWith(MockitoJUnitRunner.class)
// public class UserServiceImplTest {
    
//     private static final long USER_ID = 1L;
//     private static final String USER_EMAIL = "sabrina@sns.com";
//     private static final String USER_PASSWORD = "shortandsweet";
    
//     @InjectMocks
//     private UserServiceImpl us;

//     @Mock
//     private UserDao userDaoMock;

//     @Test
//     public void testChangePasswordByIDNonexistentUser(){
//         Mockito.when(userDaoMock.getUserById(Mockito.eq(USER_ID))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             us.changePasswordByID(USER_ID, USER_PASSWORD)
//         );
//     }

//     @Test
//     public void testAskPasswordRecoverNonexistentUser(){
//         Mockito.when(userDaoMock.getUserByEmail(Mockito.eq(USER_EMAIL))).thenReturn(Optional.empty());

//         Assert.assertThrows(NotFoundException.class, () -> 
//             us.askPasswordRecover(USER_EMAIL)
//         );
//     }

// }