package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJpaDaoTest {
    
    @Autowired
    private UserJpaDao userDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testGetUserById(){
        final User USER = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER.setId(USER_ID);
        USER.getPicture().setId(TestData.Images.validImageId);

        Optional<User> foundUser = userDao.getUserById(USER_ID);

        Assert.assertNotNull(foundUser);
        Assert.assertTrue(foundUser.isPresent());
        Assert.assertEquals(USER, foundUser.get());
        Assert.assertEquals(USER.getEmail(), foundUser.get().getEmail());
        Assert.assertEquals(USER.getPassword(), foundUser.get().getPassword());
        Assert.assertEquals(USER.getName(), foundUser.get().getName());
        Assert.assertEquals(USER.getRole(), foundUser.get().getRole());
        Assert.assertEquals(USER.getTelephone(), foundUser.get().getTelephone());
        Assert.assertEquals(USER.getLocale(), foundUser.get().getLocale());
        Assert.assertEquals(USER.getPicture().getId(), foundUser.get().getPicture().getId());
    }

    @Test
    public void testGetUserByIdNonexistent(){
        final long USER_ID = TestData.Users.newPatientId;

        Optional<User> foundUser = userDao.getUserById(USER_ID);

        Assert.assertNotNull(foundUser);
        Assert.assertFalse(foundUser.isPresent());
    }

    @Test
    public void testGetUserByEmail(){
        final User USER = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER.setId(USER_ID);
        USER.getPicture().setId(TestData.Images.validImageId);
        final String USEREMAIL = TestData.Users.patient.getEmail();

        Optional<User> foundUser = userDao.getUserByEmail(USEREMAIL);

        Assert.assertNotNull(foundUser);
        Assert.assertTrue(foundUser.isPresent());
        Assert.assertEquals(USER, foundUser.get());
        Assert.assertEquals(USER.getEmail(), foundUser.get().getEmail());
        Assert.assertEquals(USER.getPassword(), foundUser.get().getPassword());
        Assert.assertEquals(USER.getName(), foundUser.get().getName());
        Assert.assertEquals(USER.getRole(), foundUser.get().getRole());
        Assert.assertEquals(USER.getTelephone(), foundUser.get().getTelephone());
        Assert.assertEquals(USER.getLocale(), foundUser.get().getLocale());
        Assert.assertEquals(USER.getPicture().getId(), foundUser.get().getPicture().getId());
    }

    @Test
    public void testGetUserByEmailNull(){

        Optional<User> foundUser = userDao.getUserByEmail(null);

        Assert.assertNotNull(foundUser);
        Assert.assertFalse(foundUser.isPresent());
    }

    @Test
    public void testGetUserByEmailEmpty(){

        Optional<User> foundUser = userDao.getUserByEmail("");

        Assert.assertNotNull(foundUser);
        Assert.assertFalse(foundUser.isPresent());
    }

    @Test
    public void testGetUserByEmailNonexistent(){
        final String USEREMAIL = TestData.Users.newPatient.getEmail();

        Optional<User> foundUser = userDao.getUserByEmail(USEREMAIL);

        Assert.assertNotNull(foundUser);
        Assert.assertFalse(foundUser.isPresent());
    }

    @Test
    public void testChangePasswordByID(){
        final long USER_ID = TestData.Users.patientId;
        final String PASSWORD = TestData.Users.patient.getPassword();
        final String NEW_PASSWORD = PASSWORD + "1";

        userDao.changePasswordByID(USER_ID, NEW_PASSWORD);
        Patient userPersisted = em.find(Patient.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(NEW_PASSWORD, userPersisted.getPassword());
        Assert.assertNotEquals(PASSWORD, userPersisted.getPassword());
    }

    @Test
    public void testChangePasswordByIDNonexistentUser(){
        final long USER_ID = 0;
        final String PASSWORD = TestData.Users.patient.getPassword();

        userDao.changePasswordByID(USER_ID, PASSWORD);
        User userPersisted = em.find(User.class, USER_ID);

        Assert.assertNull(userPersisted);
    }

    @Test
    public void testChangePasswordByIDNullPass(){
        final long USER_ID = TestData.Users.patientId;
        final String PASSWORD = TestData.Users.patient.getPassword();
        final String NEW_PASSWORD = null;

        userDao.changePasswordByID(USER_ID, NEW_PASSWORD);
        Patient userPersisted = em.find(Patient.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertNotEquals(NEW_PASSWORD, userPersisted.getPassword());
        Assert.assertEquals(PASSWORD, userPersisted.getPassword());
    }

    @Test
    public void testChangePasswordByIDVoidPass(){
        final long USER_ID = TestData.Users.patientId;
        final String PASSWORD = TestData.Users.patient.getPassword();
        final String NEW_PASSWORD = "";

        userDao.changePasswordByID(USER_ID, NEW_PASSWORD);
        Patient userPersisted = em.find(Patient.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertNotEquals(NEW_PASSWORD, userPersisted.getPassword());
        Assert.assertEquals(PASSWORD, userPersisted.getPassword());
    }

}