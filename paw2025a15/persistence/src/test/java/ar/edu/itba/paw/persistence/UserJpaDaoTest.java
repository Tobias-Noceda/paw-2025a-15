/*package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
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
    public void testCreate(){
        final String USEREMAIL = TestData.Users.newPatient.getEmail();
        final String PASSWORD = TestData.Users.newPatient.getPassword();
        final String USERNAME = TestData.Users.newPatient.getName();
        final String USER_TELEPHONE = TestData.Users.newPatient.getTelephone();
        final UserRoleEnum USER_ROLE = TestData.Users.newPatient.getRole();
        final File PICTURE = TestData.Users.newPatient.getPicture();
        final LocaleEnum USER_LOCALE = TestData.Users.newPatient.getLocale();

        User user = userDao.create(USEREMAIL, PASSWORD, USERNAME, USER_TELEPHONE, USER_ROLE, PICTURE, USER_LOCALE);
        User userPersisted = em.find(User.class, user.getId());

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USEREMAIL, userPersisted.getEmail());
        Assert.assertEquals(PASSWORD, userPersisted.getPassword());
        Assert.assertEquals(USERNAME, userPersisted.getName());
        Assert.assertEquals(USER_TELEPHONE, userPersisted.getTelephone());
        Assert.assertEquals(USER_ROLE, userPersisted.getRole());
        Assert.assertEquals(PICTURE, userPersisted.getPicture());
        Assert.assertEquals(USER_LOCALE, userPersisted.getLocale());

    }
}
*/