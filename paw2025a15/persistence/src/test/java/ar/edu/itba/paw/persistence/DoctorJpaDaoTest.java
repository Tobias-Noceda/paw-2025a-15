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
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
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
        final User DOC = TestData.Users.doctor;
        final Long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        final Long PIC_ID = TestData.Images.validImageId;
        final String LICENCE = TestData.DoctorDetails.doctorDetail.getDoctorLicense();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();

        Doctor doctor = doctorDao.createDoctor(
            "dulcedeleche@example.com",
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
        Assert.assertEquals("dulcedeleche@example.com", doctorPersisted.getEmail());
        Assert.assertEquals(DOC.getName(), doctorPersisted.getName());
        Assert.assertEquals(DOC.getTelephone(), doctorPersisted.getTelephone());
        Assert.assertEquals(PIC_ID, doctorPersisted.getPicture().getId());
        Assert.assertEquals(DOC.getLocale(), doctorPersisted.getLocale());
        Assert.assertEquals(LICENCE, doctorPersisted.getLicence());
        Assert.assertEquals(SPECIALTY, doctorPersisted.getSpecialty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testCreateExistentDetail(){
        final User DOC = TestData.Users.doctor;
        final Long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        final Long PIC_ID = TestData.Images.validImageId;
        final String LICENCE = TestData.DoctorDetails.doctorDetail.getDoctorLicense();
        final SpecialtyEnum SPECIALTY = TestData.DoctorDetails.doctorDetail.getSpecialty();

        Assert.assertThrows(PersistenceException.class,()->{
            doctorDao.createDoctor(
            "dulcedeleche@example.com",
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

}
