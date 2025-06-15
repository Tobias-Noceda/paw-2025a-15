package ar.edu.itba.paw.persistence;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DoctorJpaDaoTest {
    
    @Autowired
    private DoctorJpaDao doctorDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Sql({"classpath:images.sql"})
    public void testCreate(){
        final Doctor DOC = TestData.Users.doctor;
        final Long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        final Long PIC_ID = TestData.Images.validImageId;
        final String LICENCE = DOC.getLicence();
        final SpecialtyEnum SPECIALTY = DOC.getSpecialty();
        final String EMAIL = "dulcedeleche@example.com";

        Doctor doctor = doctorDao.createDoctor(
            EMAIL,
            DOC.getPassword(),
            DOC.getName(),
            DOC.getTelephone(),
            PIC_ID,
            DOC.getLocale(),
            LICENCE,
            SPECIALTY,
            new ArrayList<>()
        );
        Doctor doctorPersisted = em.find(Doctor.class, doctor.getId());

        Assert.assertNotNull(doctorPersisted);
        Assert.assertEquals(EMAIL, doctorPersisted.getEmail());
        Assert.assertEquals(DOC.getPassword(), doctorPersisted.getPassword());
        Assert.assertEquals(DOC.getName(), doctorPersisted.getName());
        Assert.assertEquals(DOC.getTelephone(), doctorPersisted.getTelephone());
        Assert.assertEquals(PIC_ID, doctorPersisted.getPicture().getId());
        Assert.assertEquals(DOC.getLocale(), doctorPersisted.getLocale());
        Assert.assertEquals(LICENCE, doctorPersisted.getLicence());
        Assert.assertEquals(SPECIALTY, doctorPersisted.getSpecialty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testCreateExistentDoc(){
        final Doctor DOC = TestData.Users.doctor;
        final Long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        final Long PIC_ID = TestData.Images.validImageId;
        final String LICENCE = DOC.getLicence();
        final SpecialtyEnum SPECIALTY = DOC.getSpecialty();

        Assert.assertThrows(PersistenceException.class,()->{
            doctorDao.createDoctor(
            DOC.getEmail(),
            DOC.getPassword(),
            DOC.getName(),
            DOC.getTelephone(),
            PIC_ID,
            DOC.getLocale(),
            LICENCE,
            SPECIALTY,
            new ArrayList<>()
            );
            em.flush();
        });
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetDoctorById(){
        final long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        DOC.getPicture().setId(TestData.Images.validImageId);

        Optional<Doctor> foundDoctor = doctorDao.getDoctorById(DOC_ID);

        Assert.assertNotNull(foundDoctor);
        Assert.assertTrue(foundDoctor.isPresent());
        Assert.assertEquals(DOC, foundDoctor.get());
        Assert.assertEquals(DOC.getEmail(), foundDoctor.get().getEmail());
        Assert.assertEquals(DOC.getPassword(), foundDoctor.get().getPassword());
        Assert.assertEquals(DOC.getName(), foundDoctor.get().getName());
        Assert.assertEquals(DOC.getTelephone(), foundDoctor.get().getTelephone());
        Assert.assertEquals(DOC.getPicture().getId(), foundDoctor.get().getPicture().getId());
        Assert.assertEquals(DOC.getLocale(), foundDoctor.get().getLocale());
        Assert.assertEquals(DOC.getLicence(), foundDoctor.get().getLicence());
        Assert.assertEquals(DOC.getSpecialty(), foundDoctor.get().getSpecialty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testUpdateDoctor(){
        final Long PIC_ID = TestData.Images.validImage2Id;
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.getPicture().setId(TestData.Images.validImageId);
        Insurance INSURANCE = TestData.Insurances.validInsurance;
        final Long I_PIC_ID =  TestData.Images.validImageId;
        INSURANCE.setPicture(em.find(File.class, I_PIC_ID));
        INSURANCE = em.merge(INSURANCE); 
        List<Insurance> INSURANCES = List.of(INSURANCE);

        doctorDao.updateDoctor(DOC_ID, "1155555555", PIC_ID, LocaleEnum.ES_AR, INSURANCES);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(doctorPersisted);
        Assert.assertEquals("1155555555", doctorPersisted.getTelephone());
        Assert.assertNotEquals(DOC.getTelephone(), doctorPersisted.getTelephone());
        Assert.assertEquals(PIC_ID, doctorPersisted.getPicture().getId());
        Assert.assertNotEquals(DOC.getPicture().getId(), doctorPersisted.getPicture().getId());
        Assert.assertEquals(LocaleEnum.ES_AR, doctorPersisted.getLocale());
        Assert.assertNotEquals(DOC.getLocale(), doctorPersisted.getLocale());
        Assert.assertEquals(1, doctorPersisted.getInsurances().size());
        Assert.assertEquals(INSURANCE.getId(), doctorPersisted.getInsurances().get(0).getId());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testUpdateDoctorNullInsurances(){
        final Long PIC_ID = TestData.Images.validImage2Id;
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.getPicture().setId(TestData.Images.validImageId);

        doctorDao.updateDoctor(DOC_ID, "1155555555", PIC_ID, LocaleEnum.ES_AR, null);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(doctorPersisted);
        Assert.assertEquals("1155555555", doctorPersisted.getTelephone());
        Assert.assertNotEquals(DOC.getTelephone(), doctorPersisted.getTelephone());
        Assert.assertEquals(PIC_ID, doctorPersisted.getPicture().getId());
        Assert.assertNotEquals(DOC.getPicture().getId(), doctorPersisted.getPicture().getId());
        Assert.assertEquals(LocaleEnum.ES_AR, doctorPersisted.getLocale());
        Assert.assertNotEquals(DOC.getLocale(), doctorPersisted.getLocale());
        Assert.assertEquals(0, doctorPersisted.getInsurances().size());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testUpdateDoctorNonexistentDoctor(){
        final Long PIC_ID = TestData.Images.validImage2Id;
        final Long DOC_ID = 0L;

        doctorDao.updateDoctor(DOC_ID, "1155555555", PIC_ID, LocaleEnum.ES_AR, null);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNull(doctorPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testUpdateDoctorNonexistentPic(){
        final Long PIC_ID = 0L;
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.getPicture().setId(TestData.Images.validImageId);

        doctorDao.updateDoctor(DOC_ID, "1155555555", PIC_ID, LocaleEnum.ES_AR, null);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(doctorPersisted);
        Assert.assertNotEquals("1155555555", doctorPersisted.getTelephone());
        Assert.assertEquals(DOC.getTelephone(), doctorPersisted.getTelephone());
        Assert.assertNotEquals(PIC_ID, doctorPersisted.getPicture().getId());
        Assert.assertEquals(DOC.getPicture().getId(), doctorPersisted.getPicture().getId());
        Assert.assertNotEquals(LocaleEnum.ES_AR, doctorPersisted.getLocale());
        Assert.assertEquals(DOC.getLocale(), doctorPersisted.getLocale());
        Assert.assertEquals(0, doctorPersisted.getInsurances().size());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testUpdateDoctorNullTelephone(){
        final Long PIC_ID = TestData.Images.validImage2Id;
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.getPicture().setId(TestData.Images.validImageId);

        doctorDao.updateDoctor(DOC_ID, null, PIC_ID, LocaleEnum.ES_AR, null);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(doctorPersisted);
        Assert.assertNotEquals(null, doctorPersisted.getTelephone());
        Assert.assertEquals(DOC.getTelephone(), doctorPersisted.getTelephone());
        Assert.assertNotEquals(PIC_ID, doctorPersisted.getPicture().getId());
        Assert.assertEquals(DOC.getPicture().getId(), doctorPersisted.getPicture().getId());
        Assert.assertNotEquals(LocaleEnum.ES_AR, doctorPersisted.getLocale());
        Assert.assertEquals(DOC.getLocale(), doctorPersisted.getLocale());
        Assert.assertEquals(0, doctorPersisted.getInsurances().size());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testUpdateDoctorEmptyTelephone(){
        final Long PIC_ID = TestData.Images.validImage2Id;
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.getPicture().setId(TestData.Images.validImageId);

        doctorDao.updateDoctor(DOC_ID, "", PIC_ID, LocaleEnum.ES_AR, null);
        Doctor doctorPersisted = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(doctorPersisted);
        Assert.assertNotEquals("", doctorPersisted.getTelephone());
        Assert.assertEquals(DOC.getTelephone(), doctorPersisted.getTelephone());
        Assert.assertNotEquals(PIC_ID, doctorPersisted.getPicture().getId());
        Assert.assertEquals(DOC.getPicture().getId(), doctorPersisted.getPicture().getId());
        Assert.assertNotEquals(LocaleEnum.ES_AR, doctorPersisted.getLocale());
        Assert.assertEquals(DOC.getLocale(), doctorPersisted.getLocale());
        Assert.assertEquals(0, doctorPersisted.getInsurances().size());
    }

    @Test
    public void testGetDoctorByIdNonexistentDoc(){
        final long DOC_ID = TestData.Users.patientId;

        Optional<Doctor> foundDoctor = doctorDao.getDoctorById(DOC_ID);

        Assert.assertNotNull(foundDoctor);
        Assert.assertFalse(foundDoctor.isPresent());
    }    

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndName(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = TestData.Users.patient.getName();

        int results = doctorDao.searchAuthPatientsCountByDoctorAndName(DOC_ID, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndNameNullName(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = null;

        int results = doctorDao.searchAuthPatientsCountByDoctorAndName(DOC_ID, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndNameBlankSpaceName(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = "";

        int results = doctorDao.searchAuthPatientsCountByDoctorAndName(DOC_ID, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndName(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<Patient> results = doctorDao.searchAuthPatientsPageByDoctorAndName(DOC_ID, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameWrongPage(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = TestData.Users.patient.getName();

        List<Patient> results = doctorDao.searchAuthPatientsPageByDoctorAndName(DOC_ID, PATIENT_NAME, 0, 2);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameWrongPageSize(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = TestData.Users.patient.getName();

        List<Patient> results = doctorDao.searchAuthPatientsPageByDoctorAndName(DOC_ID, PATIENT_NAME, 1, 0);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameNullName(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = null;
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<Patient> results = doctorDao.searchAuthPatientsPageByDoctorAndName(DOC_ID, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameBlankName(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = "";
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<Patient> results = doctorDao.searchAuthPatientsPageByDoctorAndName(DOC_ID, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testLicenceExists(){
        final String DOC_LICENCE = TestData.Users.doctor.getLicence();

        boolean result = doctorDao.licenceExists(DOC_LICENCE);

        Assert.assertTrue(result);
    }

    @Test
    @Sql({"classpath:images.sql"})
    public void testLicenceExistsFalse(){
        final String DOC_LICENCE = TestData.Users.doctor.getLicence();

        boolean result = doctorDao.licenceExists(DOC_LICENCE);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testLicenceExistsNullLicence(){

        boolean result = doctorDao.licenceExists(null);

        Assert.assertFalse(result);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetDoctorsPageByParams(){
        final Long DOC_ID = TestData.Users.doctorId;
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        List<Doctor> doctors = doctorDao.getDoctorsPageByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE_ID, WEEKDAY, DoctorOrderEnum.M_POPULAR, 1, 2);

        Assert.assertNotNull(doctors);
        Assert.assertFalse(doctors.isEmpty());
        Assert.assertEquals(1, doctors.size());
        Assert.assertEquals(DOC_ID, doctors.get(0).getId());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetDoctorsPageByParamsNullName(){
        final Long DOC_ID = TestData.Users.doctorId;
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        List<Doctor> doctors = doctorDao.getDoctorsPageByParams(null, DOC_SPECIALTY, INSURANCE_ID, WEEKDAY, DoctorOrderEnum.M_POPULAR, 1, 2);

        Assert.assertNotNull(doctors);
        Assert.assertFalse(doctors.isEmpty());
        Assert.assertEquals(1, doctors.size());
        Assert.assertEquals(DOC_ID, doctors.get(0).getId());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetDoctorsPageByParamsEmptyName(){
        final Long DOC_ID = TestData.Users.doctorId;
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        List<Doctor> doctors = doctorDao.getDoctorsPageByParams("", DOC_SPECIALTY, INSURANCE_ID, WEEKDAY, DoctorOrderEnum.M_POPULAR, 1, 2);

        Assert.assertNotNull(doctors);
        Assert.assertFalse(doctors.isEmpty());
        Assert.assertEquals(1, doctors.size());
        Assert.assertEquals(DOC_ID, doctors.get(0).getId());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetDoctorsPageByParamsWrongPage(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        List<Doctor> doctors = doctorDao.getDoctorsPageByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE_ID, WEEKDAY, DoctorOrderEnum.M_POPULAR, 0, 2);

        Assert.assertNotNull(doctors);
        Assert.assertTrue(doctors.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetDoctorsPageByParamsWrongPageSize(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        List<Doctor> doctors = doctorDao.getDoctorsPageByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE_ID, WEEKDAY, DoctorOrderEnum.M_POPULAR, 1, 0);

        Assert.assertNotNull(doctors);
        Assert.assertTrue(doctors.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetTotalDoctorsByParams(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        int doctors = doctorDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE_ID, WEEKDAY);

        Assert.assertEquals(1, doctors);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetTotalDoctorsByParamsNullName(){
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        int doctors = doctorDao.getTotalDoctorsByParams(null, DOC_SPECIALTY, INSURANCE_ID, WEEKDAY);

        Assert.assertEquals(1, doctors);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetTotalDoctorsByParamsNullSpecialty(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        int doctors = doctorDao.getTotalDoctorsByParams(DOC_NAME, null, INSURANCE_ID, WEEKDAY);

        Assert.assertEquals(1, doctors);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetTotalDoctorsByParamsNullInsurance(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final Long INSURANCE_ID = 0L;

        int doctors = doctorDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE_ID, WEEKDAY);

        Assert.assertEquals(1, doctors);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
    public void testGetTotalDoctorsByParamsNullWeekday(){
        final String DOC_NAME = TestData.Users.doctor.getName();
        final SpecialtyEnum DOC_SPECIALTY = TestData.Users.doctor.getSpecialty();
        final Long INSURANCE_ID = TestData.Insurances.validInsuranceId;

        int doctors = doctorDao.getTotalDoctorsByParams(DOC_NAME, DOC_SPECIALTY, INSURANCE_ID, null);

        Assert.assertEquals(1, doctors);
    }

}
