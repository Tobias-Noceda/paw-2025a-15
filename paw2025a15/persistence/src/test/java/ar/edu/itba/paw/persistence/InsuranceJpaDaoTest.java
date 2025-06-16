package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder.In;

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
        final Long PIC_ID = TestData.Images.validImageId;

        Insurance insurance = insuranceDao.create(NAME, PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, insurance.getId());

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(NAME, insurancePersisted.getName());
        Assert.assertEquals(PIC_ID, insurancePersisted.getPicture().getId());
    }

    @Test
    public void testEdit(){
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Insurance INS = new Insurance(INSURANCE.getName(), INSURANCE.getPicture());
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);
        INS.setId(INSURANCE_ID);

        Insurance insurance = insuranceDao.edit(INS, NEW_NAME, NEW_PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(insurance.getId(), insurancePersisted.getId());
        Assert.assertEquals(insurance.getName(), insurancePersisted.getName()); 
        Assert.assertEquals(insurance.getPicture().getId(), insurancePersisted.getPicture().getId());  
    }

    @Test
    public void testEditNullInsurance(){
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);

        Insurance insurance = insuranceDao.edit(null, NEW_NAME, NEW_PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNotNull(insurancePersisted);
        Assert.assertNull(insurance);
    }

    @Test
    public void testEditNullName(){
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Insurance INS = new Insurance(INSURANCE.getName(), INSURANCE.getPicture());
        final String NEW_NAME = null;
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);
        INS.setId(INSURANCE_ID);

        Insurance insurance = insuranceDao.edit(INS, NEW_NAME, NEW_PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(insurance.getId(), insurancePersisted.getId());
        Assert.assertEquals(INSURANCE.getName(), insurancePersisted.getName()); 
        Assert.assertEquals(insurance.getPicture().getId(), insurancePersisted.getPicture().getId());  
    }

    @Test
    public void testEditEmptyName(){
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Insurance INS = new Insurance(INSURANCE.getName(), INSURANCE.getPicture());
        final String NEW_NAME = "";
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);
        INS.setId(INSURANCE_ID);

        Insurance insurance = insuranceDao.edit(INS, NEW_NAME, NEW_PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(insurance.getId(), insurancePersisted.getId());
        Assert.assertEquals(INSURANCE.getName(), insurancePersisted.getName()); 
        Assert.assertEquals(insurance.getPicture().getId(), insurancePersisted.getPicture().getId());  
    }

    @Test
    public void testEditNullPic(){
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Insurance INS = new Insurance(INSURANCE.getName(), INSURANCE.getPicture());
        final String NEW_NAME = TestData.Insurances.newInsurance.getName();
        final File NEW_PICTURE = null;
        INS.setId(INSURANCE_ID);

        Insurance insurance = insuranceDao.edit(INS, NEW_NAME, NEW_PICTURE);
        Insurance insurancePersisted = em.find(Insurance.class, INSURANCE_ID);

        Assert.assertNotNull(insurancePersisted);
        Assert.assertEquals(insurance.getId(), insurancePersisted.getId());
        Assert.assertEquals(insurance.getName(), insurancePersisted.getName()); 
        Assert.assertEquals(INSURANCE.getPicture().getId(), insurancePersisted.getPicture().getId());  
    }

    @Test
    public void testGetInsuranceById(){
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        INSURANCE.setId(INSURANCE_ID);
        INSURANCE.getPicture().setId(TestData.Images.validImageId);

        Optional<Insurance> foundInsurance = insuranceDao.getInsuranceById(INSURANCE_ID);

        Assert.assertNotNull(foundInsurance);
        Assert.assertTrue(foundInsurance.isPresent());
        Assert.assertEquals(INSURANCE.getId(), foundInsurance.get().getId());
        Assert.assertEquals(INSURANCE.getName(), foundInsurance.get().getName());
        Assert.assertEquals(INSURANCE.getPicture().getId(), foundInsurance.get().getPicture().getId());
    }

    @Test
    public void testGetInsuranceByIdNonexistentInsurance(){
        final Long INSURANCE_ID = 0L;

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
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        INSURANCE.setId(INSURANCE_ID);
        INSURANCE.getPicture().setId(TestData.Images.validImageId);

        Optional<Insurance> insurance = insuranceDao.getInsuranceByName(INSURANCE.getName());

        Assert.assertNotNull(insurance);
        Assert.assertTrue(insurance.isPresent());
        Assert.assertEquals(INSURANCE.getId(), insurance.get().getId());
        Assert.assertEquals(INSURANCE.getName(), insurance.get().getName());
        Assert.assertEquals(INSURANCE.getPicture().getId(), insurance.get().getPicture().getId());
    }

    @Test
    public void testGetInsuranceByNameNull(){
        final String INSURANCE_NAME = null;

        Optional<Insurance> foundInsurance = insuranceDao.getInsuranceByName(INSURANCE_NAME);

        Assert.assertNotNull(foundInsurance);
        Assert.assertFalse(foundInsurance.isPresent());
    }    

    @Test
    public void testGetInsuranceByNameEmpty(){
        final String INSURANCE_NAME = "";

        Optional<Insurance> foundInsurance = insuranceDao.getInsuranceByName(INSURANCE_NAME);

        Assert.assertNotNull(foundInsurance);
        Assert.assertFalse(foundInsurance.isPresent());
    }   

    @Test
    public void testGetInsurancesCount(){
        final Long INS1_ID = TestData.Insurances.validInsuranceId;
        final Long INS2_ID = TestData.Insurances.validInsurance2Id;
        final int INS_AMOUNT = 2;//in insurances.sql

        int result = insuranceDao.getInsurancesCount();
        Insurance insurance1Persisted = em.find(Insurance.class, INS1_ID);
        Insurance insurance2Persisted = em.find(Insurance.class, INS2_ID);

        Assert.assertEquals(INS_AMOUNT, result);
        Assert.assertNotNull(insurance1Persisted);
        Assert.assertNotNull(insurance2Persisted);
    }

    @Test
    public void testGetInsurancesPage(){
        final Long INS1_ID = TestData.Insurances.validInsuranceId;
        final Long INS2_ID = TestData.Insurances.validInsurance2Id;

        List<Insurance> insurances = insuranceDao.getInsurancesPage(1, 2);
        Insurance insurance1Persisted = em.find(Insurance.class, INS1_ID);
        Insurance insurance2Persisted = em.find(Insurance.class, INS2_ID);

        Assert.assertNotNull(insurances);
        Assert.assertEquals(2, insurances.size());
        Assert.assertTrue(insurances.contains(insurance2Persisted));
        Assert.assertTrue(insurances.contains(insurance1Persisted));
        Assert.assertNotNull(insurance1Persisted);
        Assert.assertNotNull(insurance2Persisted);
    }

    @Test
    public void testGetInsurancesPageWrongPage(){
        final Long INS1_ID = TestData.Insurances.validInsuranceId;
        final Long INS2_ID = TestData.Insurances.validInsurance2Id;

        List<Insurance> insurances = insuranceDao.getInsurancesPage(0, 2);
        Insurance insurance1Persisted = em.find(Insurance.class, INS1_ID);
        Insurance insurance2Persisted = em.find(Insurance.class, INS2_ID);

        Assert.assertNotNull(insurances);
        Assert.assertEquals(0, insurances.size());
        Assert.assertNotNull(insurance1Persisted);
        Assert.assertNotNull(insurance2Persisted);
    }

    @Test
    public void testGetInsurancesPageWrongPageSize(){
        final Long INS1_ID = TestData.Insurances.validInsuranceId;
        final Long INS2_ID = TestData.Insurances.validInsurance2Id;

        List<Insurance> insurances = insuranceDao.getInsurancesPage(1, 0);
        Insurance insurance1Persisted = em.find(Insurance.class, INS1_ID);
        Insurance insurance2Persisted = em.find(Insurance.class, INS2_ID);

        Assert.assertNotNull(insurances);
        Assert.assertEquals(0, insurances.size());
        Assert.assertNotNull(insurance1Persisted);
        Assert.assertNotNull(insurance2Persisted);
    }

    @Test
    public void testDelete(){
        final Long INS1_ID = TestData.Insurances.validInsuranceId;
        final Long INS2_ID = TestData.Insurances.validInsurance2Id;
        Insurance insurance1 = em.find(Insurance.class, INS1_ID);

        insuranceDao.delete(insurance1);
        Insurance insurance1Persisted = em.find(Insurance.class, INS1_ID);
        Insurance insurance2Persisted = em.find(Insurance.class, INS2_ID);

        Assert.assertNull(insurance1Persisted);
        Assert.assertNotNull(insurance2Persisted);
    }

}