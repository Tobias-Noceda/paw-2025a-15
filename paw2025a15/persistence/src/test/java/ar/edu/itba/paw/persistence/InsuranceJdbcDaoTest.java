package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:insurances.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class InsuranceJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private InsuranceJdbcDao insuranceDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        final String NAME = TestData.Insurances.newInsurance.getName();
        final long PICTURE_ID = TestData.Insurances.newInsurance.getPictureId();

        Insurance insurance = insuranceDao.create(NAME, PICTURE_ID);

        Assert.assertNotNull(insurance);
        Assert.assertEquals(NAME, insurance.getName());
        Assert.assertEquals(PICTURE_ID, insurance.getPictureId());
    }

    @Test
    public void testEdit(){
        final long INSURANCE_ID = TestData.Insurances.validInsurance.getId();
        final String NAME = TestData.Insurances.validInsurance.getName();
        final long PICTURE_ID = TestData.Insurances.validInsurance.getPictureId();
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();
        final long NEW_PICTURE_ID = TestData.Images.validImage2.getId();

        insuranceDao.edit(INSURANCE_ID, NEW_NAME, NEW_PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d AND insurance_name = '%s' AND picture_id = %d", INSURANCE_ID, NAME, PICTURE_ID)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d AND insurance_name = '%s' AND picture_id = %d", INSURANCE_ID, NEW_NAME, NEW_PICTURE_ID)));
    }

    @Test
    public void testEditNonexistentInsurance(){
        final long INSURANCE_ID =  0;
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();
        final long NEW_PICTURE_ID = TestData.Images.validImage2.getId();

        insuranceDao.edit(INSURANCE_ID, NEW_NAME, NEW_PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d", INSURANCE_ID)));
    }

    @Test
    public void testEditNameOnly(){
        final long INSURANCE_ID = TestData.Insurances.validInsurance.getId();
        final String NAME = TestData.Insurances.validInsurance.getName();
        final long PICTURE_ID = TestData.Insurances.validInsurance.getPictureId();
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();

        insuranceDao.edit(INSURANCE_ID, NEW_NAME, PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d AND insurance_name = '%s' AND picture_id = %d", INSURANCE_ID, NAME, PICTURE_ID)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d AND insurance_name = '%s' AND picture_id = %d", INSURANCE_ID, NEW_NAME, PICTURE_ID)));
    }

    @Test
    public void testEditPicOnly(){
        final long INSURANCE_ID = TestData.Insurances.validInsurance.getId();
        final String NAME = TestData.Insurances.validInsurance.getName();
        final long PICTURE_ID = TestData.Insurances.validInsurance.getPictureId();
        final long NEW_PICTURE_ID = TestData.Images.validImage2.getId();

        insuranceDao.edit(INSURANCE_ID, NAME, NEW_PICTURE_ID);

        Assert.assertEquals(0, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d AND insurance_name = '%s' AND picture_id = %d", INSURANCE_ID, NAME, PICTURE_ID)));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d AND insurance_name = '%s' AND picture_id = %d", INSURANCE_ID, NAME, NEW_PICTURE_ID)));
    }

    @Test
    public void testGetInsuranceById(){
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final long INSURANCE_ID = TestData.Insurances.validInsurance.getId();

        Optional<Insurance> foundInsurance = insuranceDao.getInsuranceById(INSURANCE_ID);

        Assert.assertTrue(foundInsurance.isPresent());
        Assert.assertEquals(INSURANCE, foundInsurance.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d", INSURANCE_ID)));

    }

    @Test
    public void testGetInsuranceByIdNonexistentInsurance(){
        final long INSURANCE_ID = 0;

        Optional<Insurance> foundInsurance = insuranceDao.getInsuranceById(INSURANCE_ID);

        Assert.assertFalse(foundInsurance.isPresent());
        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d", INSURANCE_ID)));
    }

    @Test
    public void testGetAllInsurances(){
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;

        List<Insurance> foundInsurances = insuranceDao.getAllInsurances();

        Assert.assertFalse(foundInsurances.isEmpty());
        Assert.assertEquals(2, foundInsurances.size());
        Assert.assertTrue(foundInsurances.contains(INSURANCE1));
        Assert.assertTrue(foundInsurances.contains(INSURANCE2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTable(jdbcTemplate, "insurances"));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d", INSURANCE1.getId())));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_id = %d", INSURANCE2.getId())));
    }

    @Test
    public void testGetInsuranceByName(){
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;

        Optional<Insurance> insurance = insuranceDao.getInsuranceByName(INSURANCE1.getName());

        Assert.assertNotNull(insurance);
        Assert.assertEquals(INSURANCE1, insurance.get());
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "insurances", String.format("insurance_name LIKE '%s'", INSURANCE1.getName())));
    }

}
