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

import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
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

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testGetTotalDoctors(){

        int result = doctorDetailDao.getTotalDoctors();

        Assert.assertEquals(1, result);
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTable(jdbcTemplate, "doctor_details"));    
    }

    @Test
    public void testGetTotalDoctorsNoDoctors(){

        int result = doctorDetailDao.getTotalDoctors();

        Assert.assertEquals(0, result);
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTable(jdbcTemplate, "doctor_details"));    
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testGetTotalDoctorsByName(){
        final String NAME = TestData.Users.doctor.getName();

        int result = doctorDetailDao.getTotalDoctorsByName(NAME);

        Assert.assertEquals(1, result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetTotalDoctorsByNameNoDoctors(){
        final String NAME = TestData.Users.doctor.getName();

        int result = doctorDetailDao.getTotalDoctorsByName(NAME);

        Assert.assertEquals(0, result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetTotalFilteredDoctors(){
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();

        int result = doctorDetailDao.getTotalFilteredDoctors(SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(1, result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql"})
    public void testGetTotalFilteredDoctorsNoDoctors(){
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();

        int result = doctorDetailDao.getTotalFilteredDoctors(SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(0, result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetTotalFilteredDoctorsNoDoctorsByInsurance(){
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final Insurance INSURANCE = TestData.Insurances.newInsurance;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();

        int result = doctorDetailDao.getTotalFilteredDoctors(SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(0, result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetTotalFilteredDoctorsNoDoctorsByWeekday(){
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final WeekdayEnum WEEKDAY = WeekdayEnum.SUNDAY;

        int result = doctorDetailDao.getTotalFilteredDoctors(SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(0, result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetTotalFilteredDoctorsNoDoctorsBySpecialty(){
        final SpecialtyEnum SPECIALTY = SpecialtyEnum.UROLOGY;
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();

        int result = doctorDetailDao.getTotalFilteredDoctors(SPECIALTY, INSURANCE, WEEKDAY);

        Assert.assertEquals(0, result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testGetAuthDoctorsByPatientId(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOCTOR_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long PICTURE_ID = TestData.Users.doctor.getPictureId();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();
        final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();

        List<DoctorView> foundDocs = doctorDetailDao.getAuthDoctorsByPatientId(PATIENT_ID);

        Assert.assertFalse(foundDocs.isEmpty());
        Assert.assertEquals(1, foundDocs.size());
        Assert.assertEquals(DOCTOR_ID, foundDocs.getFirst().getId());
        Assert.assertEquals(DOC_NAME, foundDocs.getFirst().getName());
        Assert.assertEquals(SPECIALTY, foundDocs.getFirst().getSpecialty());
        Assert.assertEquals(PICTURE_ID, foundDocs.getFirst().getImageId());
        Assert.assertFalse(foundDocs.getFirst().getInsurances().isEmpty());
        Assert.assertEquals(2, foundDocs.getFirst().getInsurances().size());
        Assert.assertTrue(foundDocs.getFirst().getInsurances().contains(INSURANCE1));
        Assert.assertTrue(foundDocs.getFirst().getInsurances().contains(INSURANCE2));
        Assert.assertFalse(foundDocs.getFirst().getWeekdays().isEmpty());
        Assert.assertEquals(1, foundDocs.getFirst().getWeekdays().size());
        Assert.assertTrue(foundDocs.getFirst().getWeekdays().contains(WEEKDAY));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthDoctorsByPatientIdWithoutAuths(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();

        List<DoctorView> foundDocs = doctorDetailDao.getAuthDoctorsByPatientId(PATIENT_ID);

        Assert.assertTrue(foundDocs.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthDoctorsByPatientIdNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();

        List<DoctorView> foundDocs = doctorDetailDao.getAuthDoctorsByPatientId(PATIENT_ID);

        Assert.assertTrue(foundDocs.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        boolean result = doctorDetailDao.hasAuthDoctor(PATIENT_ID, DOC_ID);

        Assert.assertTrue(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testHasAuthDoctorNoAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        boolean result = doctorDetailDao.hasAuthDoctor(PATIENT_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        boolean result = doctorDetailDao.hasAuthDoctor(PATIENT_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorNonexistentDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.Users.newDoctor.getId();

        boolean result = doctorDetailDao.hasAuthDoctor(PATIENT_ID, DOC_ID);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevel(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = doctorDetailDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertTrue(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testHasAuthDoctorWithAccessLevelNoAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = doctorDetailDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = doctorDetailDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testHasAuthDoctorWithAccessLevelNonexistentDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.Users.newDoctor.getId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        boolean result = doctorDetailDao.hasAuthDoctorWithAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        doctorDetailDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        doctorDetailDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testAuthDoctorExistentBasicAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        doctorDetailDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonExistentBasicAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = AccessLevelEnum.VIEW_SOCIAL;

        doctorDetailDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);

        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
        Assert.assertEquals(1, 
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        Assert.assertThrows(DataIntegrityViolationException.class, () -> {
            doctorDetailDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);});
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testAuthDoctorNonexistentDocNonexistentDoc(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.Users.newDoctor.getId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        Assert.assertThrows(DataIntegrityViolationException.class, () -> {
            doctorDetailDao.authDoctor(PATIENT_ID, DOC_ID, ACCES_LEVEL);});
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorAllLevelsBasicOnly(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        doctorDetailDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d", DOC_ID, PATIENT_ID)));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testUnauthDoctorAllLevelsNoAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        doctorDetailDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d", DOC_ID, PATIENT_ID)));

    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorAllLevelsMultiple(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        doctorDetailDao.unauthDoctorAllAccessLevels(PATIENT_ID, DOC_ID);

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d", DOC_ID, PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql"})
    public void testUnauthDoctorByAccessLevelBasicOnly(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        doctorDetailDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);;

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleSocialErase(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        doctorDetailDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);;

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, ACCES_LEVEL.ordinal())));
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d AND access_level = %d", DOC_ID, PATIENT_ID, AccessLevelEnum.VIEW_BASIC.ordinal())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testUnauthDoctorByAccessLevelMultipleBasicErase(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();

        doctorDetailDao.unauthDoctorByAccessLevel(PATIENT_ID, DOC_ID, ACCES_LEVEL);;

        Assert.assertEquals(0, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "auth_doctors", String.format("doctor_id = %d AND patient_id = %d", DOC_ID, PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnums(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();
        final AccessLevelEnum ACCES_LEVEL = TestData.AuthDoctors.authDoctor.getAccessLevel();
        final AccessLevelEnum ACCES_LEVEL_SOCIAL = TestData.AuthDoctors.authDoctorSocialLevel.getAccessLevel();

        List<AccessLevelEnum> foundAccessLevels =doctorDetailDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);

        Assert.assertFalse(foundAccessLevels.isEmpty());
        Assert.assertEquals(2, foundAccessLevels.size());
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL));
        Assert.assertTrue(foundAccessLevels.contains(ACCES_LEVEL_SOCIAL));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql"})
    public void testGetAuthAccessLevelEnumsNoAuth(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        List<AccessLevelEnum> foundAccessLevels =doctorDetailDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);

        Assert.assertTrue(foundAccessLevels.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentPatient(){
        final long PATIENT_ID = TestData.Users.newPatient.getId();
        final long DOC_ID = TestData.AuthDoctors.authDoctor.getDoctorId();

        List<AccessLevelEnum> foundAccessLevels =doctorDetailDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);

        Assert.assertTrue(foundAccessLevels.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:insurances.sql", "classpath:users.sql", "classpath:doctorDetails.sql", "classpath:doctorCoverages.sql", "classpath:doctorShifts.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testGetAuthAccessLevelEnumsNonexistentDoctor(){
        final long PATIENT_ID = TestData.AuthDoctors.authDoctor.getPatientId();
        final long DOC_ID = TestData.Users.newDoctor.getId();

        List<AccessLevelEnum> foundAccessLevels =doctorDetailDao.getAuthAccessLevelEnums(PATIENT_ID, DOC_ID);

        Assert.assertTrue(foundAccessLevels.isEmpty());
    }
}
