package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.LocaleEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private UserJdbcDao userDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        final String USEREMAIL = TestData.Users.newPatient.getEmail();
        final String PASSWORD = TestData.Users.newPatient.getPassword();
        final String USERNAME = TestData.Users.newPatient.getName();
        final String USER_TELEPHONE = TestData.Users.newPatient.getTelephone();
        final UserRoleEnum USER_ROLE = TestData.Users.newPatient.getRole();
        final long PICTURE_ID = TestData.Users.newPatient.getPictureId();
        final LocaleEnum USER_LOCALE = TestData.Users.newPatient.getLocale();

        User user = userDao.create(USEREMAIL, PASSWORD, USERNAME, USER_TELEPHONE, USER_ROLE, PICTURE_ID, USER_LOCALE);

        Assert.assertNotNull(user);
        Assert.assertEquals(USEREMAIL, user.getEmail());
        Assert.assertEquals(PASSWORD, user.getPassword());
        Assert.assertEquals(USERNAME, user.getName());
        Assert.assertEquals(USER_TELEPHONE, user.getTelephone());
        Assert.assertEquals(USER_ROLE, user.getRole());
        Assert.assertEquals(PICTURE_ID, user.getPictureId());
        Assert.assertEquals(USER_LOCALE, user.getLocale());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_email = '%s'", USEREMAIL)));

    }

    @Test
    public void testCreateExistentEmail(){
        final String USEREMAIL = TestData.Users.patient.getEmail();
        final String PASSWORD = TestData.Users.newPatient.getPassword();
        final String USERNAME = TestData.Users.newPatient.getName();
        final String USER_TELEPHONE = TestData.Users.newPatient.getTelephone();
        final UserRoleEnum USER_ROLE = TestData.Users.newPatient.getRole();
        final long PICTURE_ID = TestData.Users.newPatient.getPictureId();
        final LocaleEnum USER_LOCALE = TestData.Users.newPatient.getLocale();
//TODO preguntar si el service o el dao es el que tienen que tener la programacion defensiva de esto
        Assert.assertThrows(DuplicateKeyException.class,()->{
                                userDao.create(USEREMAIL, PASSWORD, USERNAME, USER_TELEPHONE, USER_ROLE, PICTURE_ID, USER_LOCALE);});
    }

    @Test
    public void testGetUserById(){
        final User USER = TestData.Users.patient;
        final long USER_ID = TestData.Users.patient.getId();

        Optional<User> foundUser = userDao.getUserById(USER_ID);

        Assert.assertTrue(foundUser.isPresent());
        Assert.assertEquals(USER, foundUser.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d", USER_ID)));

    }

    @Test
    public void testGetUserByIdNonexistent(){
        final long USER_ID = TestData.Users.newPatient.getId();

        Optional<User> foundUser = userDao.getUserById(USER_ID);

        Assert.assertFalse(foundUser.isPresent());
        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d", USER_ID)));

    }

    @Test
    public void testGetUserByEmail(){
        final User USER = TestData.Users.patient;
        final String USEREMAIL = TestData.Users.patient.getEmail();

        Optional<User> foundUser = userDao.getUserByEmail(USEREMAIL);

        Assert.assertTrue(foundUser.isPresent());
        Assert.assertEquals(USER, foundUser.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_email = '%s'", USEREMAIL)));

    }

    @Test
    public void testGetUserByEmailNonexistent(){
        final String USEREMAIL = TestData.Users.newPatient.getEmail();

        Optional<User> foundUser = userDao.getUserByEmail(USEREMAIL);

        Assert.assertFalse(foundUser.isPresent());
        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_email = '%s'", USEREMAIL)));

    }

    @Test
    public void testChangePassword(){
        final String USEREMAIL = TestData.Users.patient.getEmail();
        final String PASSWORD = TestData.Users.patient.getPassword();
        final String NEW_PASSWORD = PASSWORD + "1";

        userDao.changePassword(USEREMAIL, NEW_PASSWORD);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_email = '%s' AND user_password = '%s'", USEREMAIL, PASSWORD)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_email = '%s' AND user_password = '%s'", USEREMAIL, NEW_PASSWORD)));  
    }

    @Test
    public void testChangePasswordNonexistentUser(){
        final String USEREMAIL = "";
        final String PASSWORD = TestData.Users.patient.getPassword();

        userDao.changePassword(USEREMAIL, PASSWORD);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_email = '%s'", USEREMAIL)));
    }

    @Test
    public void testChangePasswordByID(){
        final long USER_ID = TestData.Users.patient.getId();
        final String PASSWORD = TestData.Users.patient.getPassword();
        final String NEW_PASSWORD = PASSWORD + "1";

        userDao.changePasswordByID(USER_ID, NEW_PASSWORD);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_password = '%s'", USER_ID, PASSWORD)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_password = '%s'", USER_ID, NEW_PASSWORD)));  
    }

    @Test
    public void testChangePasswordByIDNonexistentUser(){
        final long USER_ID = 0;
        final String PASSWORD = TestData.Users.patient.getPassword();

        userDao.changePasswordByID(USER_ID, PASSWORD);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d", USER_ID)));
    }

    @Test
    public void testUpdatePhoneNumber(){
        final long USER_ID = TestData.Users.patient.getId();
        final String USER_TELEPHONE = TestData.Users.patient.getTelephone();
        final String NEW_TELEPHONE = "1111111111";

        userDao.updatePhoneNumber(USER_ID, NEW_TELEPHONE);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_telephone = '%s'", USER_ID, USER_TELEPHONE)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_telephone = '%s'", USER_ID, NEW_TELEPHONE)));  
    }

    @Test
    public void testUpdatePhoneNumberNonexistentUser(){
        final long USER_ID = 0;
        final String NEW_TELEPHONE = "1111111111";

        userDao.updatePhoneNumber(USER_ID, NEW_TELEPHONE);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d", USER_ID)));
    }

    @Test
    public void testEditUser(){
        final long USER_ID = TestData.Users.patient.getId();
        final String USER_TELEPHONE = TestData.Users.patient.getTelephone();
        final String USERNAME = TestData.Users.patient.getName();
        final long PICTURE_ID = TestData.Users.patient.getPictureId();
        final String NEW_TELEPHONE = "1111111111";
        final String NEW_USERNAME = USERNAME + "1";
        final long NEW_PICTURE_ID = TestData.Images.validImage2.getId();

        userDao.editUser(USER_ID, NEW_USERNAME, NEW_TELEPHONE, NEW_PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_name = '%s' AND user_telephone = '%s' AND picture_id = %d", USER_ID, USERNAME, USER_TELEPHONE, PICTURE_ID)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_name = '%s' AND user_telephone = '%s' AND picture_id = %d", USER_ID, NEW_USERNAME, NEW_TELEPHONE, NEW_PICTURE_ID)));
    }

    @Test
    public void testEditUserNonexistentUser(){
        final long USER_ID = 0;
        final String USERNAME = TestData.Users.patient.getName();
        final String NEW_TELEPHONE = "1111111111";
        final String NEW_USERNAME = USERNAME + "1";
        final long NEW_PICTURE_ID = TestData.Images.validImage2.getId();

        userDao.editUser(USER_ID, NEW_USERNAME, NEW_TELEPHONE, NEW_PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d", USER_ID)));
   }

    @Test
    public void testEditUserNameOnly(){
        final long USER_ID = TestData.Users.patient.getId();
        final String USER_TELEPHONE = TestData.Users.patient.getTelephone();
        final String USERNAME = TestData.Users.patient.getName();
        final long PICTURE_ID = TestData.Users.patient.getPictureId();
        final String NEW_USERNAME = USERNAME + "1";

        userDao.editUser(USER_ID, NEW_USERNAME, USER_TELEPHONE, PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_name = '%s' AND user_telephone = '%s' AND picture_id = %d", USER_ID, USERNAME, USER_TELEPHONE, PICTURE_ID)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_name = '%s' AND user_telephone = '%s' AND picture_id = %d", USER_ID, NEW_USERNAME, USER_TELEPHONE, PICTURE_ID)));
    }

    @Test
    public void testEditUserTelephoneOnly(){
        final long USER_ID = TestData.Users.patient.getId();
        final String USER_TELEPHONE = TestData.Users.patient.getTelephone();
        final String USERNAME = TestData.Users.patient.getName();
        final long PICTURE_ID = TestData.Users.patient.getPictureId();
        final String NEW_TELEPHONE = "1111111111";

        userDao.editUser(USER_ID, USERNAME, NEW_TELEPHONE, PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_name = '%s' AND user_telephone = '%s' AND picture_id = %d", USER_ID, USERNAME, USER_TELEPHONE, PICTURE_ID)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_name = '%s' AND user_telephone = '%s' AND picture_id = %d", USER_ID, USERNAME, NEW_TELEPHONE, PICTURE_ID)));
    }

    @Test
    public void testEditUserPicOnly(){
        final long USER_ID = TestData.Users.patient.getId();
        final String USER_TELEPHONE = TestData.Users.patient.getTelephone();
        final String USERNAME = TestData.Users.patient.getName();
        final long PICTURE_ID = TestData.Users.patient.getPictureId();
        final long NEW_PICTURE_ID = TestData.Images.validImage2.getId();

        userDao.editUser(USER_ID, USERNAME, USER_TELEPHONE, NEW_PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_name = '%s' AND user_telephone = '%s' AND picture_id = %d", USER_ID, USERNAME, USER_TELEPHONE, PICTURE_ID)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND user_name = '%s' AND user_telephone = '%s' AND picture_id = %d", USER_ID, USERNAME, USER_TELEPHONE, NEW_PICTURE_ID)));
    }

    @Test
    public void testUpdateLocale(){
        final long USER_ID = TestData.Users.patient.getId();
        final LocaleEnum LOCALE = TestData.Users.patient.getLocale();
        final LocaleEnum NEW_LOCALE = LocaleEnum.ES_AR;

        userDao.updateLocale(USER_ID, NEW_LOCALE);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND locale = %d", USER_ID, LOCALE.ordinal())));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d AND locale = %d", USER_ID, NEW_LOCALE.ordinal())));  
    }

    @Test
    public void testUpdateLocaleNonexistentUser(){
        final long USER_ID = 0;
        final LocaleEnum NEW_LOCALE = LocaleEnum.ES_AR;

        userDao.updateLocale(USER_ID, NEW_LOCALE);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("user_id = %d", USER_ID)));
    }
}
