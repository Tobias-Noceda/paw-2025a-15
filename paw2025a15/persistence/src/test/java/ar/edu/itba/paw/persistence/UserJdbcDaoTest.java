package ar.edu.itba.paw.persistence;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {

    private static final String PASSWORD = "Password";
    private static final String EMAIL = "Email";

    @Autowired
    private DataSource ds;

    @Autowired
    private UserJdbcDao userDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    public void testCreate() {
        final User user = userDao.create(EMAIL, PASSWORD);

        Assert.assertNotNull(user);
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals(PASSWORD, user.getPassword());

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));
    }
}
