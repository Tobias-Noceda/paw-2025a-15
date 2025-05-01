package ar.edu.itba.paw.persistence;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import ar.edu.itba.paw.models.LocaleEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
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
}
