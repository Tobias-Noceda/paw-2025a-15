package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.entities.DoctorCoverage;
import ar.edu.itba.paw.models.entities.DoctorCoverageId;
import ar.edu.itba.paw.models.entities.DoctorDetail;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DoctorDetailJpaDaoTest {
    
    @Autowired
    private DoctorDetailJpaDao doctorDetailDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testCreate(){
        final User DOC = TestData.Users.doctor;
        final long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        final String LICENCE = TestData.DoctorDetails.doctorDetail.getDoctorLicense();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();

        DoctorDetail doctorDetail = doctorDetailDao.create(DOC.getId(), LICENCE, SPECIALTY);
        DoctorDetail doctorDetailPersisted = em.find(DoctorDetail.class, doctorDetail.getDoctorId());

        Assert.assertNotNull(doctorDetailPersisted);
        Assert.assertEquals(DOC, doctorDetailPersisted.getDoctor());
        Assert.assertEquals(LICENCE, doctorDetailPersisted.getDoctorLicense());
        Assert.assertEquals(SPECIALTY, doctorDetailPersisted.getSpecialty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testCreateExistentDetail(){
        final long DOC_ID = TestData.Users.doctorId;
        final String LICENCE = TestData.DoctorDetails.doctorDetail.getDoctorLicense();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();

        Assert.assertThrows(PersistenceException.class,()->{
            doctorDetailDao.create(DOC_ID, LICENCE, SPECIALTY);
            em.flush();
        });
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testGetDetailByDoctorId(){
        final long DOC_ID = TestData.Users.doctorId;
        final DoctorDetail DETAIL = TestData.DoctorDetails.doctorDetail;
        DETAIL.setDoctorId(DOC_ID);
        DETAIL.getDoctor().getPicture().setId(TestData.Images.validImageId);

        Optional<DoctorDetail> foundDetail = doctorDetailDao.getDetailByDoctorId(DOC_ID);

        Assert.assertNotNull(foundDetail);
        Assert.assertTrue(foundDetail.isPresent());
        Assert.assertEquals(DETAIL, foundDetail.get());
    }

    @Test
    public void testGetDetailByDoctorIdNonexistentDoc(){
        final long DOC_ID = TestData.Users.patientId;

        Optional<DoctorDetail> foundDetail = doctorDetailDao.getDetailByDoctorId(DOC_ID);

        Assert.assertNotNull(foundDetail);
        Assert.assertFalse(foundDetail.isPresent());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql"})
    public void testAddDoctorCoverage(){
        final long DOC_ID = TestData.Users.doctorId;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final DoctorCoverage DC = TestData.DoctorCoverages.doctorCoverage;
        DC.setDoctor(TestData.Users.doctor);
        DC.getDoctor().setId(DOC_ID);
        DC.getDoctor().getPicture().setId(TestData.Images.validImageId);
        DC.setInsurance(TestData.Insurances.validInsurance);
        DC.getInsurance().setId(INSURANCE_ID);

        doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
        DoctorCoverage dcPersisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));

        Assert.assertNotNull(dcPersisted);
        Assert.assertEquals(DC, dcPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql"})
    public void testAddCoverageNonexistentDoctor(){
        final long DOC_ID = TestData.Users.patientId;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
        DoctorCoverage dcPersisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));
        
        Assert.assertNull(dcPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testAddCoverageNonexistentInsurance(){
        final long DOC_ID = TestData.Users.doctorId;
        final long INSURANCE_ID = 99L;

        doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
        DoctorCoverage dcPersisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));
        
        Assert.assertNull(dcPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql"})
    public void testAddCoverages(){
        final long DOC_ID = TestData.Users.doctorId;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final long INSURANCE2_ID = TestData.Insurances.validInsurance2Id;
        final DoctorCoverage DC = TestData.DoctorCoverages.doctorCoverage;
        DC.getDoctorCoverageId().setDoctorId(DOC_ID);
        DC.getDoctorCoverageId().setInsuranceId(INSURANCE_ID);
        final DoctorCoverage DC2 = TestData.DoctorCoverages.doctorCoverage2;
        DC2.getDoctorCoverageId().setDoctorId(DOC_ID);
        DC2.getDoctorCoverageId().setInsuranceId(INSURANCE2_ID);

        final List<Long> INSURANCES_IDS = List.of(INSURANCE_ID, INSURANCE2_ID);

        int[] results = doctorDetailDao.addDoctorCoverages(DOC_ID, INSURANCES_IDS);
        DoctorCoverage dcPersisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));
        DoctorCoverage dc2Persisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE2_ID));
        
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
        Assert.assertNotNull(dcPersisted);
        Assert.assertNotNull(dc2Persisted);
        Assert.assertEquals(DC, dcPersisted);
        Assert.assertEquals(DC2, dc2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testRemoveDoctorCoverages(){
        final long DOC_ID = TestData.Users.doctorId;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final long INSURANCE2_ID = TestData.Insurances.validInsurance2Id;
        final DoctorCoverage DC2 = TestData.DoctorCoverages.doctorCoverage2;
        DC2.setDoctor(TestData.Users.doctor);
        DC2.getDoctor().setId(DOC_ID);
        DC2.getDoctor().getPicture().setId(TestData.Images.validImageId);
        DC2.setInsurance(TestData.Insurances.validInsurance2);
        DC2.getInsurance().setId(INSURANCE2_ID);

        final List<Long> INSURANCES_IDS = List.of(INSURANCE_ID);

        doctorDetailDao.removeDoctorCoverages(DOC_ID, INSURANCES_IDS);
        DoctorCoverage dcFound = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));
        DoctorCoverage dc2Found = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE2_ID));
        
        Assert.assertNull(dcFound);
        Assert.assertNotNull(dc2Found);
        Assert.assertEquals(DC2, dc2Found);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testRemoveAllCoveragesForDoctorId(){
        final long DOC_ID = TestData.Users.doctorId;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final long INSURANCE2_ID = TestData.Insurances.validInsurance2Id;

        doctorDetailDao.removeAllCoveragesForDoctorId(DOC_ID);
        DoctorCoverage dcFound = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));
        DoctorCoverage dc2Found = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE2_ID));
        
        Assert.assertNull(dcFound);
        Assert.assertNull(dc2Found);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testAddCoverageExistentDocCov(){
        final long DOC_ID = TestData.Users.doctorId;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        Assert.assertThrows(PersistenceException.class,()->{
            doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
            em.flush();
        });
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testGetInsurancesById(){
        final long DOC_ID = TestData.Users.doctorId;
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
        final long INSURANCE1_ID = TestData.Insurances.validInsuranceId;
        INSURANCE1.setId(INSURANCE1_ID);
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        final long INSURANCE2_ID = TestData.Insurances.validInsurance2Id;
        INSURANCE2.setId(INSURANCE2_ID);

        List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);
        Insurance insuranceFound = em.find(Insurance.class, INSURANCE1_ID);
        Insurance insurance2Found = em.find(Insurance.class, INSURANCE2_ID);

        Assert.assertFalse(foundInsurances.isEmpty());
        Assert.assertEquals(2, foundInsurances.size());
        Assert.assertNotNull(insuranceFound);
        Assert.assertNotNull(insurance2Found);
        Assert.assertTrue(foundInsurances.contains(insuranceFound));
        Assert.assertTrue(foundInsurances.contains(insurance2Found));
        Assert.assertEquals(INSURANCE1, insuranceFound);
        Assert.assertEquals(INSURANCE2, insurance2Found);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql"})
    public void testGetInsurancesByIdNonexistentDoctor(){
        final long DOC_ID = TestData.Users.patientId;

        List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);

        Assert.assertTrue(foundInsurances.isEmpty());
    }
/*TODO:continue this tests
    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorShifts.sql", "classpath:doctorCoverages.sql"})
    public void testGetTotalDoctorsByParams(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        INSURANCE.setId(TestData.Insurances.validInsuranceId);

        int result = doctorDetailDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(1, result);
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_weekday = %d AND doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE LOWER(users.user_name) LIKE LOWER('%s') AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", WEEKDAY.ordinal(), DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
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
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_weekday = %d AND doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE LOWER(users.user_name) LIKE LOWER('%s') AND doctor_coverages.insurance_id = %d)", WEEKDAY.ordinal(), DOC_NAME, INSURANCE.getId())));
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
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_weekday = %d AND doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE LOWER(users.user_name) LIKE LOWER('%s') AND doctor_details.doctor_specialty = %d)", WEEKDAY.ordinal(), DOC_NAME, DOC_SPECIALTY.ordinal())));
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
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE LOWER(users.user_name) LIKE LOWER('%s') AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
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
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE LOWER(users.user_name) LIKE LOWER('%s') AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
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
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE LOWER(users.user_name) LIKE LOWER('%s') AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
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
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id IN (SELECT doctor_details.doctor_id FROM users JOIN doctor_details ON users.user_id = doctor_details.doctor_id JOIN doctor_coverages ON doctor_details.doctor_id = doctor_coverages.doctor_id WHERE LOWER(users.user_name) LIKE LOWER('%s') AND doctor_details.doctor_specialty = %d AND doctor_coverages.insurance_id = %d)", DOC_NAME, DOC_SPECIALTY.ordinal(), INSURANCE.getId())));
    }*/
}
