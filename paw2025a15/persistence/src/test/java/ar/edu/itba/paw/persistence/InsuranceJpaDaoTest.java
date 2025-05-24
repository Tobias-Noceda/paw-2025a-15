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
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class InsuranceJpaDaoTest {
    
    @Autowired
    private InsuranceJpaDao insuranceDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testCreate(){
        final String NAME = TestData.Insurances.newInsurance.getName();
        final File PICTURE = TestData.Insurances.newInsurance.getPicture();

        Insurance insurance = insuranceDao.create(NAME, PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, insurance.getId());

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(NAME, insurancePersisted.getName());
        Assert.assertEquals(PICTURE, insurancePersisted.getPicture());
    }

}
*/