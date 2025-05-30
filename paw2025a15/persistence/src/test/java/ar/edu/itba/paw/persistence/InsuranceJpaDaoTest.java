package ar.edu.itba.paw.persistence;

import java.util.List;
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

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:insurances.sql")
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

    @Test
    public void testEdit(){
        final Insurance INSURANCE_OLD = TestData.Insurances.validInsurance;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        INSURANCE_OLD.setId(INSURANCE_ID);
        INSURANCE_OLD.getPicture().setId(TestData.Images.validImageId);
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);
        final Insurance INSURANCE_NEW = new Insurance(NEW_NAME, NEW_PICTURE);
        INSURANCE_NEW.setId(INSURANCE_ID);

        insuranceDao.edit(INSURANCE_ID, NEW_NAME, NEW_PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(INSURANCE_NEW, insurancePersisted);
        Assert.assertNotEquals(INSURANCE_OLD, insurancePersisted);   
    }

    @Test
    public void testEditNonexistentInsurance(){
        final long INSURANCE_ID =  0;
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);

        insuranceDao.edit(INSURANCE_ID, NEW_NAME, NEW_PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNull(insurancePersisted);
    }

    @Test
    public void testEditNameOnly(){
        final Insurance INSURANCE_OLD = TestData.Insurances.validInsurance;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        INSURANCE_OLD.setId(INSURANCE_ID);
        INSURANCE_OLD.getPicture().setId(TestData.Images.validImageId);
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();
        final File PICTURE = INSURANCE_OLD.getPicture();
        final Insurance INSURANCE_NEW = new Insurance(NEW_NAME, PICTURE);
        INSURANCE_NEW.setId(INSURANCE_ID);

        insuranceDao.edit(INSURANCE_ID, NEW_NAME, PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(INSURANCE_NEW, insurancePersisted);
        Assert.assertNotEquals(INSURANCE_OLD, insurancePersisted);   
    }

    @Test
    public void testEditPicOnly(){
        final Insurance INSURANCE_OLD = TestData.Insurances.validInsurance;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        INSURANCE_OLD.setId(INSURANCE_ID);
        INSURANCE_OLD.getPicture().setId(TestData.Images.validImageId);
        final String NAME = TestData.Insurances.validInsurance.getName();
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);
        final Insurance INSURANCE_NEW = new Insurance(NAME, NEW_PICTURE);
        INSURANCE_NEW.setId(INSURANCE_ID);

        insuranceDao.edit(INSURANCE_ID, NAME, NEW_PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(INSURANCE_NEW.getPicture(), insurancePersisted.getPicture());
        Assert.assertNotEquals(INSURANCE_OLD.getPicture(), insurancePersisted.getPicture());   
    }

    @Test
    public void testGetInsuranceById(){
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        INSURANCE.setId(INSURANCE_ID);
        INSURANCE.getPicture().setId(TestData.Images.validImageId);

        Optional<Insurance> foundInsurance = insuranceDao.getInsuranceById(INSURANCE_ID);

        Assert.assertNotNull(foundInsurance);
        Assert.assertTrue(foundInsurance.isPresent());
        Assert.assertEquals(INSURANCE, foundInsurance.get());
    }

    @Test
    public void testGetInsuranceByIdNonexistentInsurance(){
        final long INSURANCE_ID = 0;

        Optional<Insurance> foundInsurance = insuranceDao.getInsuranceById(INSURANCE_ID);

        Assert.assertNotNull(foundInsurance);
        Assert.assertFalse(foundInsurance.isPresent());
    }

    @Test
    public void testGetAllInsurances(){
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
        INSURANCE1.setId(TestData.Insurances.validInsuranceId);
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        INSURANCE2.setId(TestData.Insurances.validInsurance2Id);

        List<Insurance> foundInsurances = insuranceDao.getAllInsurances();

        Assert.assertFalse(foundInsurances.isEmpty());
        Assert.assertEquals(2, foundInsurances.size());
        Assert.assertTrue(foundInsurances.contains(INSURANCE1));
        Assert.assertTrue(foundInsurances.contains(INSURANCE2));
    }

    @Test
    public void testGetInsuranceByName(){
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        INSURANCE.setId(INSURANCE_ID);
        INSURANCE.getPicture().setId(TestData.Images.validImageId);

        Optional<Insurance> insurance = insuranceDao.getInsuranceByName(INSURANCE.getName());

        Assert.assertNotNull(insurance);
        Assert.assertTrue(insurance.isPresent());
        Assert.assertEquals(INSURANCE, insurance.get());
    }

    @Test
    public void testGetInsuranceByNameNull(){
        final String INSURANCE_NAME = null;

        Optional<Insurance> foundInsurance = insuranceDao.getInsuranceByName(INSURANCE_NAME);

        Assert.assertNotNull(foundInsurance);
        Assert.assertFalse(foundInsurance.isPresent());
    }    

}