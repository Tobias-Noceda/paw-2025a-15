package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DoctorDetailJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private DoctorDetailJdbcDao doctorDetailDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        final long DOC_ID = TestData.DoctorDetails.doctorDetail.getDoctorId();
        final String LICENCE = TestData.DoctorDetails.doctorDetail.getLicence();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();

        DoctorDetail doctorDetail = doctorDetailDao.create(DOC_ID, LICENCE, SPECIALTY);

        Assert.assertNotNull(doctorDetail);
        Assert.assertEquals(DOC_ID, doctorDetail.getDoctorId());
        Assert.assertEquals(LICENCE, doctorDetail.getLicence());
        Assert.assertEquals(SPECIALTY, doctorDetail.getSpecialty());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_details", String.format("doctor_id = %d", DOC_ID)));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testCreateExistentDetail(){
        final long DOC_ID = TestData.DoctorDetails.doctorDetail.getDoctorId();
        final String LICENCE = TestData.DoctorDetails.doctorDetail.getLicence();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();

        Assert.assertThrows(DuplicateKeyException.class,()->{
            doctorDetailDao.create(DOC_ID, LICENCE, SPECIALTY);});
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testGetDetailByDoctorId(){
        final long DOC_ID = TestData.DoctorDetails.doctorDetail.getDoctorId();
        final DoctorDetail DETAIL = TestData.DoctorDetails.doctorDetail;

        Optional<DoctorDetail> foundDetail = doctorDetailDao.getDetailByDoctorId(DOC_ID);

        Assert.assertTrue(foundDetail.isPresent());
        Assert.assertEquals(DETAIL, foundDetail.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_details", String.format("doctor_id = %d", DOC_ID)));

    }

    @Test
    public void testGetDetailByDoctorIdNonexistentDoc(){
        final long DOC_ID = TestData.DoctorDetails.doctorDetail.getDoctorId();

        Optional<DoctorDetail> foundDetail = doctorDetailDao.getDetailByDoctorId(DOC_ID);

        Assert.assertFalse(foundDetail.isPresent());
        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_details", String.format("doctor_id = %d", DOC_ID)));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql"})
    public void testAddDoctorCoverage(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final long INSURANCE_ID = TestData.DoctorCoverages.doctorCoverage.getInsuranceId();

        doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);

        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d AND insurance_id = %d", DOC_ID, INSURANCE_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql"})
    public void testAddCoverageNonexistentDoctor(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final long INSURANCE_ID = TestData.DoctorCoverages.doctorCoverage.getInsuranceId();

        Assert.assertThrows(DataIntegrityViolationException.class, () -> {
            doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
        });
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testAddCoverageNonexistentInsurance(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final long INSURANCE_ID = TestData.DoctorCoverages.doctorCoverage.getInsuranceId();

        Assert.assertThrows(DataIntegrityViolationException.class, () -> {
            doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
        });
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testAddCoverageExistentDocCov(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final long INSURANCE_ID = TestData.DoctorCoverages.doctorCoverage.getInsuranceId();

        Assert.assertThrows(DuplicateKeyException.class,()->{
            doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);});
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testRemoveCoverageExistentDocCov(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final long INSURANCE_ID = TestData.DoctorCoverages.doctorCoverage.getInsuranceId();

        boolean result = doctorDetailDao.removeDoctorCoverage(DOC_ID, INSURANCE_ID);

        Assert.assertTrue(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql"})
    public void testRemoveCoverageExistentDocCovNonexistent(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final long INSURANCE_ID = TestData.DoctorCoverages.doctorCoverage.getInsuranceId();

        boolean result = doctorDetailDao.removeDoctorCoverage(DOC_ID, INSURANCE_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testGetInsurancesById(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;

        List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);

        Assert.assertFalse(foundInsurances.isEmpty());
        Assert.assertEquals(2, foundInsurances.size());
        Assert.assertTrue(foundInsurances.contains(INSURANCE1));
        Assert.assertTrue(foundInsurances.contains(INSURANCE2));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql"})
    public void testGetInsurancesByIdNonexistentDoctor(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();

        List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);

        Assert.assertTrue(foundInsurances.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql"})
    public void testGetInsurancesByIdNonexistentCoveragesDoctor(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();

        List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);

        Assert.assertTrue(foundInsurances.isEmpty());
    }
    
}
