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

import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
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
        final String LICENCE = TestData.DoctorDetails.doctorDetail.getDoctorLicense();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();

        DoctorDetail doctorDetail = doctorDetailDao.create(DOC_ID, LICENCE, SPECIALTY);

        Assert.assertNotNull(doctorDetail);
        Assert.assertEquals(DOC_ID, doctorDetail.getDoctorId());
        Assert.assertEquals(LICENCE, doctorDetail.getDoctorLicense());
        Assert.assertEquals(SPECIALTY, doctorDetail.getSpecialty());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_details", String.format("doctor_id = %d", DOC_ID)));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testCreateExistentDetail(){
        final long DOC_ID = TestData.DoctorDetails.doctorDetail.getDoctorId();
        final String LICENCE = TestData.DoctorDetails.doctorDetail.getDoctorLicense();
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
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql"})
    public void testAddCoverages(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final long INSURANCE_ID = TestData.DoctorCoverages.doctorCoverage.getInsuranceId();
        final long INSURANCE2_ID = TestData.DoctorCoverages.doctorCoverage2.getInsuranceId();
        final List<Long> INSURANCES_IDS = List.of(INSURANCE_ID, INSURANCE2_ID);

        int[] results = doctorDetailDao.addDoctorCoverages(DOC_ID, INSURANCES_IDS);
        
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d AND insurance_id = %d", DOC_ID, INSURANCE_ID)));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d AND insurance_id = %d", DOC_ID, INSURANCE2_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testRemoveDoctorCoverages(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final long INSURANCE_ID = TestData.DoctorCoverages.doctorCoverage.getInsuranceId();
        final long INSURANCE2_ID = TestData.DoctorCoverages.doctorCoverage2.getInsuranceId();
        final List<Long> INSURANCES_IDS = List.of(INSURANCE_ID);

        doctorDetailDao.removeDoctorCoverages(DOC_ID, INSURANCES_IDS);

        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d AND insurance_id = %d", DOC_ID, INSURANCE_ID)));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d AND insurance_id = %d", DOC_ID, INSURANCE2_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testRemoveAllCoveragesForDoctorId(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();

        doctorDetailDao.removeAllCoveragesForDoctorId(DOC_ID);
        
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d ", DOC_ID)));
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
    public void testGetInsurancesById(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;

        List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);

        Assert.assertFalse(foundInsurances.isEmpty());
        Assert.assertEquals(2, foundInsurances.size());
        Assert.assertTrue(foundInsurances.contains(INSURANCE1));
        Assert.assertTrue(foundInsurances.contains(INSURANCE2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d", DOC_ID)));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d AND insurance_id = %d", DOC_ID, INSURANCE1.getId())));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d AND insurance_id = %d", DOC_ID, INSURANCE2.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql"})
    public void testGetInsurancesByIdNonexistentDoctor(){
        final long DOC_ID = TestData.DoctorCoverages.doctorCoverage.getDoctorId();

        List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);

        Assert.assertTrue(foundInsurances.isEmpty());
    
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_coverages", String.format("doctor_id = %d", DOC_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetTotalDoctorsByParams(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;

        int result = doctorDetailDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(1, result);
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_weekday = %d AND doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE users.user_name LIKE '%s' AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", WEEKDAY.ordinal(), DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetTotalDoctorsByParamsNullName(){
        final String DOC_NAME = null;
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;

        int result = doctorDetailDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(1, result);
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_weekday = %d AND doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", WEEKDAY.ordinal(), DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetTotalDoctorsByParamsEmptyName(){
        final String DOC_NAME = "";
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;

        int result = doctorDetailDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(1, result);
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_weekday = %d AND doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", WEEKDAY.ordinal(), DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetTotalDoctorsByParamsNullSpecialty(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = null;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;

        int result = doctorDetailDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(1, result);
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_weekday = %d AND doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE users.user_name LIKE '%s' AND doctor_coverages.insurance_id = %d)", WEEKDAY.ordinal(), DOC_NAME, INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetTotalDoctorsByParamsNullInsurance(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = null;

        int result = doctorDetailDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(1, result);
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_weekday = %d AND doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE users.user_name LIKE '%s' AND doctor_details.doctor_specialty = %d)", WEEKDAY.ordinal(), DOC_NAME, DOC_SPECIALTY.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetTotalDoctorsByParamsNullWeekday(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = null;
        final Insurance INSURANCE = TestData.Insurances.validInsurance;

        int result = doctorDetailDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(1, result);
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE users.user_name LIKE '%s' AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetDoctorsPageByParams(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        final DoctorView DOC = new DoctorView(DOC_ID, DOC_NAME, DOC_SPECIALTY, 1L, List.of(INSURANCE, INSURANCE2), List.of(WEEKDAY));
        final DoctorOrderEnum DOC_ORDER = DoctorOrderEnum.L_RECENT;

        List<DoctorView> results = doctorDetailDao.getDoctorsPageByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY, DOC_ORDER, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(DOC, results.get(0));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE users.user_name LIKE '%s' AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetDoctorsPageByParamsNullName(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        final DoctorView DOC = new DoctorView(DOC_ID, DOC_NAME, DOC_SPECIALTY, 1L, List.of(INSURANCE, INSURANCE2), List.of(WEEKDAY));
        final DoctorOrderEnum DOC_ORDER = DoctorOrderEnum.L_RECENT;

        List<DoctorView> results = doctorDetailDao.getDoctorsPageByParams(null, DOC_SPECIALTY, INSURANCE, WEEKDAY, DOC_ORDER, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(DOC, results.get(0));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetDoctorsPageByParamsEmptyName(){
        final long DOC_ID = TestData.Users.doctor.getId();
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        final DoctorView DOC = new DoctorView(DOC_ID, DOC_NAME, DOC_SPECIALTY, 1L, List.of(INSURANCE, INSURANCE2), List.of(WEEKDAY));
        final DoctorOrderEnum DOC_ORDER = DoctorOrderEnum.L_RECENT;

        List<DoctorView> results = doctorDetailDao.getDoctorsPageByParams("", DOC_SPECIALTY, INSURANCE, WEEKDAY, DOC_ORDER, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(DOC, results.get(0));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetDoctorsPageByParamsWrongPage(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final DoctorOrderEnum DOC_ORDER = DoctorOrderEnum.L_RECENT;

        List<DoctorView> results = doctorDetailDao.getDoctorsPageByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY, DOC_ORDER, 0, 2);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE users.user_name LIKE '%s' AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetDoctorsPageByParamsWrongPageSize(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final DoctorOrderEnum DOC_ORDER = DoctorOrderEnum.L_RECENT;

        List<DoctorView> results = doctorDetailDao.getDoctorsPageByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY, DOC_ORDER, 1, 0);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE users.user_name LIKE '%s' AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }
}
