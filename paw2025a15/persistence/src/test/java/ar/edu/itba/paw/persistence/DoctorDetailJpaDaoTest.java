package ar.edu.itba.paw.persistence;

import java.util.ArrayList;
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

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorCoverage;
import ar.edu.itba.paw.models.entities.DoctorCoverageId;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
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
        final Doctor DOC = TestData.Users.doctor;
        Doctor doctor = doctorDetailDao.createDoctor(
            "dulcedeleche@example.com",
            DOC.getPassword(),
            DOC.getName(),
            DOC.getTelephone(),
            DOC.getPicture(),
            DOC.getLocale(),
            DOC.getLicence(),
            DOC.getSpecialty(),
            new ArrayList<>()
        );
        Doctor doctorPersisted = em.find(Doctor.class, doctor.getId());

        Assert.assertNotNull(doctorPersisted);
        Assert.assertNotEquals(DOC, doctorPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testCreateExistentDetail(){
        final Doctor DOC = TestData.Users.doctor;

        Assert.assertThrows(PersistenceException.class,()->{
            doctorDetailDao.createDoctor(DOC.getEmail(), DOC.getPassword(), DOC.getName(), DOC.getTelephone(), DOC.getPicture(), DOC.getLocale(), DOC.getLicence(), DOC.getSpecialty(), new ArrayList<>());
            em.flush();
        });
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorDetails.sql"})
    public void testGetDoctorById(){
        final long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        DOC.getPicture().setId(TestData.Images.validImageId);

        Optional<Doctor> foundDoctor = doctorDetailDao.getDoctorById(DOC_ID);

        Assert.assertNotNull(foundDoctor);
        Assert.assertTrue(foundDoctor.isPresent());
        Assert.assertEquals(DOC, foundDoctor.get());
    }

    @Test
    public void testGetDoctorByIdNonexistentDoc(){
        final long DOC_ID = TestData.Users.patientId;

        Optional<Doctor> foundDoctor = doctorDetailDao.getDoctorById(DOC_ID);

        Assert.assertNotNull(foundDoctor);
        Assert.assertFalse(foundDoctor.isPresent());
    }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate (esta función ya no existe)
    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql"})
    // public void testAddDoctorCoverage(){
    //     final long DOC_ID = TestData.Users.doctorId;
    //     final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
    //     final DoctorCoverage DC = TestData.DoctorCoverages.doctorCoverage;
    //     DC.getDoctorCoverageId().setDoctorId(DOC_ID);
    //     DC.getDoctorCoverageId().setInsuranceId(INSURANCE_ID);

    //     doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
    //     DoctorCoverage dcPersisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));

    //     Assert.assertNotNull(dcPersisted);
    //     Assert.assertEquals(DC, dcPersisted);
    // }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate (esta función ya no existe)
    // @Test
    // @Sql({"classpath:images.sql", "classpath:insurances.sql"})
    // public void testAddCoverageNonexistentDoctor(){
    //     final long DOC_ID = TestData.Users.patientId;
    //     final long INSURANCE_ID = TestData.Insurances.validInsuranceId;

    //     doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
    //     DoctorCoverage dcPersisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));
        
    //     Assert.assertNull(dcPersisted);
    // }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate (esta función ya no existe)
    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql"})
    // public void testAddCoverageNonexistentInsurance(){
    //     final long DOC_ID = TestData.Users.doctorId;
    //     final long INSURANCE_ID = 99L;

    //     doctorDetailDao.addDoctorCoverage(DOC_ID, INSURANCE_ID);
    //     DoctorCoverage dcPersisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));
        
    //     Assert.assertNull(dcPersisted);
    // }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql"})
    public void testAddCoverages(){
        final Doctor DOC = TestData.Users.doctor;
        final Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
        final DoctorCoverage DC = TestData.DoctorCoverages.doctorCoverage;
        DC.getDoctorCoverageId().setDoctorId(DOC.getId());
        DC.getDoctorCoverageId().setInsuranceId(INSURANCE.getId());
        final DoctorCoverage DC2 = TestData.DoctorCoverages.doctorCoverage2;
        DC2.getDoctorCoverageId().setDoctorId(DOC.getId());
        DC2.getDoctorCoverageId().setInsuranceId(INSURANCE2.getId());

        final List<Insurance> INSURANCES = List.of(INSURANCE, INSURANCE2);

        doctorDetailDao.updateDoctor(DOC, DOC.getTelephone(), DOC.getPicture(), DOC.getLocale(), INSURANCES);
        DoctorCoverage dcPersisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC.getId(), INSURANCE.getId()));
        DoctorCoverage dc2Persisted = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC.getId(), INSURANCE2.getId()));
        
        Assert.assertNotNull(dcPersisted);
        Assert.assertNotNull(dc2Persisted);
        Assert.assertEquals(DC, dcPersisted);
        Assert.assertEquals(DC2, dc2Persisted);
    }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate (esta función ya no existe)
    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    // public void testRemoveDoctorCoverages(){
    //     final long DOC_ID = TestData.Users.doctorId;
    //     final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
    //     final long INSURANCE2_ID = TestData.Insurances.validInsurance2Id;
    //     final DoctorCoverage DC2 = TestData.DoctorCoverages.doctorCoverage2;
    //     DC2.getDoctorCoverageId().setDoctorId(DOC_ID);
    //     DC2.getDoctorCoverageId().setInsuranceId(INSURANCE2_ID);

    //     final List<Long> INSURANCES_IDS = List.of(INSURANCE_ID);

    //     doctorDetailDao.removeDoctorCoverages(DOC_ID, INSURANCES_IDS);
    //     DoctorCoverage dcFound = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE_ID));
    //     DoctorCoverage dc2Found = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC_ID, INSURANCE2_ID));
        
    //     Assert.assertNull(dcFound);
    //     Assert.assertNotNull(dc2Found);
    //     Assert.assertEquals(DC2, dc2Found);
    // }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    public void testRemoveAllCoveragesForDoctorId(){
        final Doctor DOC = TestData.Users.doctor;
        final long INSURANCE_ID = TestData.Insurances.validInsuranceId;
        final long INSURANCE2_ID = TestData.Insurances.validInsurance2Id;

        doctorDetailDao.updateDoctor(DOC, DOC.getTelephone(), DOC.getPicture(), DOC.getLocale(), List.of());
        DoctorCoverage dcFound = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC.getId(), INSURANCE_ID));
        DoctorCoverage dc2Found = em.find(DoctorCoverage.class, new DoctorCoverageId(DOC.getId(), INSURANCE2_ID));
        
        Assert.assertNull(dcFound);
        Assert.assertNull(dc2Found);
    }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate (esta función ya no existe)
    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    // public void testAddCoverageExistentDocCov(){
    //     final Doctor DOC = TestData.Users.doctor;
    //     final Insurance INSURANCE = TestData.Insurances.validInsurance;

    //     Assert.assertThrows(PersistenceException.class,()->{
    //         doctorDetailDao.addDoctorCoverage(DOC, INSURANCE);
    //         em.flush();
    //     });
    // }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate
    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:insurances.sql", "classpath:doctorCoverages.sql"})
    // public void testGetInsurancesById(){
    //     final long DOC_ID = TestData.Users.doctorId;
    //     final Insurance INSURANCE1 = TestData.Insurances.validInsurance;
    //     final long INSURANCE1_ID = TestData.Insurances.validInsuranceId;
    //     INSURANCE1.setId(INSURANCE1_ID);
    //     final Insurance INSURANCE2 = TestData.Insurances.validInsurance2;
    //     final long INSURANCE2_ID = TestData.Insurances.validInsurance2Id;
    //     INSURANCE2.setId(INSURANCE2_ID);

    //     List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);
    //     Insurance insuranceFound = em.find(Insurance.class, INSURANCE1_ID);
    //     Insurance insurance2Found = em.find(Insurance.class, INSURANCE2_ID);

    //     Assert.assertFalse(foundInsurances.isEmpty());
    //     Assert.assertEquals(2, foundInsurances.size());
    //     Assert.assertNotNull(insuranceFound);
    //     Assert.assertNotNull(insurance2Found);
    //     Assert.assertTrue(foundInsurances.contains(insuranceFound));
    //     Assert.assertTrue(foundInsurances.contains(insurance2Found));
    //     Assert.assertEquals(INSURANCE1, insuranceFound);
    //     Assert.assertEquals(INSURANCE2, insurance2Found);
    // }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate
    // @Test
    // @Sql({"classpath:images.sql", "classpath:insurances.sql"})
    // public void testGetInsurancesByIdNonexistentDoctor(){
    //     final long DOC_ID = TestData.Users.patientId;

    //     List<Insurance> foundInsurances = doctorDetailDao.getDoctorInsurancesById(DOC_ID);

    //     Assert.assertTrue(foundInsurances.isEmpty());
    // }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndName(){
        final Doctor DOC = TestData.Users.doctor;
        final String PATIENT_NAME = TestData.Users.patient.getName();
        int results = doctorDetailDao.searchAuthPatientsCountByDoctorAndName(DOC, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndNameNullName(){
        final Doctor DOC = TestData.Users.doctor;
        final String PATIENT_NAME = null;

        int results = doctorDetailDao.searchAuthPatientsCountByDoctorAndName(DOC, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndNameBlankSpaceName(){
        final Doctor DOC = TestData.Users.doctor;
        final String PATIENT_NAME = "";

        int results = doctorDetailDao.searchAuthPatientsCountByDoctorAndName(DOC, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndName(){
        final Doctor DOC = TestData.Users.doctor;
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<Patient> results = doctorDetailDao.searchAuthPatientsPageByDoctorAndName(DOC, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameWrongPage(){
        final Doctor DOC = TestData.Users.doctor;
        final String PATIENT_NAME = TestData.Users.patient.getName();

        List<Patient> results = doctorDetailDao.searchAuthPatientsPageByDoctorAndName(DOC, PATIENT_NAME, 0, 2);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameWrongPageSize(){
        final Doctor DOC = TestData.Users.doctor;
        final String PATIENT_NAME = TestData.Users.patient.getName();

        List<Patient> results = doctorDetailDao.searchAuthPatientsPageByDoctorAndName(DOC, PATIENT_NAME, 1, 0);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameNullName(){
        final Doctor DOC = TestData.Users.doctor;
        final String PATIENT_NAME = null;
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<Patient> results = doctorDetailDao.searchAuthPatientsPageByDoctorAndName(DOC, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameBlankName(){
        final Doctor DOC = TestData.Users.doctor;
        final String PATIENT_NAME = "";
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<Patient> results = doctorDetailDao.searchAuthPatientsPageByDoctorAndName(DOC, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
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
