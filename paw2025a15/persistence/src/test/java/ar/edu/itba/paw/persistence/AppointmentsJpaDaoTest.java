package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
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

import ar.edu.itba.paw.models.entities.Appointment;
import ar.edu.itba.paw.models.entities.AppointmentId;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AppointmentsJpaDaoTest {

    @Autowired
    private AppointmentJpaDao appointmentDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void testAddAppointment(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final long PATIENT_ID = TestData.Users.patientId;
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final Appointment APP = TestData.Appointments.appointment;
        APP.getAppointmentId().setShiftId(SHIFT_ID);
        APP.getShift().setId(SHIFT_ID);
        APP.getShift().getDoctor().setId(TestData.Users.doctorId);
        APP.getPatient().setId(PATIENT_ID);
        APP.getPatient().getPicture().setId(TestData.Images.validImageId);

        Appointment appointment = appointmentDao.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE);
        Appointment appPersisted = em.find(Appointment.class, appointment.getAppointmentId());

        Assert.assertNotNull(appPersisted);
        Assert.assertEquals(APP, appPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testAddAppointmentNonexistentShift(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final long PATIENT_ID = TestData.Users.patientId;
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();

        Appointment appointment = appointmentDao.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE);
        Appointment appPersisted = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE));

        Assert.assertNull(appointment);
        Assert.assertNull(appPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql"})
    public void testGetAppointmentsByShiftIdAndDate(){
        final DoctorShift SHIFT = TestData.DoctorShifts.doctorShift;
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        SHIFT.setId(SHIFT_ID);
        final User PATIENT = TestData.Users.patient;
        final long PATIENT_ID = TestData.Users.patientId;
        PATIENT.setId(PATIENT_ID);
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final Appointment APP = TestData.Appointments.appointment;
        APP.getAppointmentId().setShiftId(SHIFT_ID);
        APP.getShift().setId(SHIFT_ID);
        APP.getShift().getDoctor().setId(TestData.Users.doctorId);
        APP.getShift().getDoctor().getPicture().setId(TestData.Images.validImageId);
        APP.setPatient(PATIENT);

        Optional<Appointment> appointment = appointmentDao.getAppointmentsByShiftIdAndDate(SHIFT_ID, APP_DATE);

        Assert.assertNotNull(appointment);
        Assert.assertTrue(appointment.isPresent());
        Assert.assertEquals(APP, appointment.get());
    }
/*
    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testGetFutureAppointmentDataByPatientId(){
        final long PATIENT_ID = TestData.Users.patientId;
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final long DOCTOR_ID = TestData.Users.doctorId;
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2Id;
        final LocalTime START_TIME2 = TestData.DoctorShifts.doctorShift2.getStartTime();
        final LocalTime END_TIME2 = TestData.DoctorShifts.doctorShift2.getEndTime();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final LocalDate APP_DATE_OLD = TestData.Appointments.oldAppointment.getDate();
        final AppointmentData APP_DATA = new AppointmentData(SHIFT_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME, END_TIME, ADDRESS);
        final AppointmentData APP_DATA2 = new AppointmentData(SHIFT2_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME2, END_TIME2, ADDRESS);

        List<AppointmentData> results = appointmentDao.getFutureAppointmentDataByPatientId(PATIENT_ID);
        Appointment appFound1 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE));
        Appointment appFound2 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE));
        Appointment appFound3 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE_OLD));
        Appointment appFound4 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE_OLD));

        Assert.assertNotNull(results);
        Assert.assertNotNull(appFound1);
        Assert.assertNotNull(appFound2);
        Assert.assertNotNull(appFound3);
        Assert.assertNotNull(appFound4);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(APP_DATA));
        Assert.assertTrue(results.contains(APP_DATA2));
    }*/
/*
    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testGetOldAppointmentDataByPatientId(){
        final long PATIENT_ID = TestData.Users.patientId;
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final long DOCTOR_ID = TestData.Users.doctorId;
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2Id;
        final LocalTime START_TIME2 = TestData.DoctorShifts.doctorShift2.getStartTime();
        final LocalTime END_TIME2 = TestData.DoctorShifts.doctorShift2.getEndTime();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalDate APP_DATE = TestData.Appointments.oldAppointment.getDate();
        final LocalDate APP_DATE_NEW = TestData.Appointments.appointment.getDate();
        final AppointmentData APP_DATA = new AppointmentData(SHIFT_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME, END_TIME, ADDRESS);
        final AppointmentData APP_DATA2 = new AppointmentData(SHIFT2_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME2, END_TIME2, ADDRESS);

        List<AppointmentData> results = appointmentDao.getOldAppointmentDataByPatientId(PATIENT_ID);
        Appointment appFound1 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE));
        Appointment appFound2 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE));
        Appointment appFound3 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE_NEW));
        Appointment appFound4 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE_NEW));

        Assert.assertNotNull(results);
        Assert.assertNotNull(appFound1);
        Assert.assertNotNull(appFound2);
        Assert.assertNotNull(appFound3);
        Assert.assertNotNull(appFound4);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(APP_DATA));
        Assert.assertTrue(results.contains(APP_DATA2));
   }
*//*
    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testGetFutureAppointmentDataByDoctorId(){
        final long PATIENT_ID = TestData.Users.patientId;
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final long DOCTOR_ID = TestData.Users.doctorId;
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2Id;
        final LocalTime START_TIME2 = TestData.DoctorShifts.doctorShift2.getStartTime();
        final LocalTime END_TIME2 = TestData.DoctorShifts.doctorShift2.getEndTime();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final LocalDate APP_DATE_OLD = TestData.Appointments.oldAppointment.getDate();
        final AppointmentData APP_DATA = new AppointmentData(SHIFT_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME, END_TIME, ADDRESS);
        final AppointmentData APP_DATA2 = new AppointmentData(SHIFT2_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME2, END_TIME2, ADDRESS);

        List<AppointmentData> results = appointmentDao.getFutureAppointmentDataByDoctorId(DOCTOR_ID);
        Appointment appFound1 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE));
        Appointment appFound2 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE));
        Appointment appFound3 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE_OLD));
        Appointment appFound4 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE_OLD));

        Assert.assertNotNull(results);
        Assert.assertNotNull(appFound1);
        Assert.assertNotNull(appFound2);
        Assert.assertNotNull(appFound3);
        Assert.assertNotNull(appFound4);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(APP_DATA));
        Assert.assertTrue(results.contains(APP_DATA2));
    }*/

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testGetAppointmentsForDate(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final long PATIENT_ID = TestData.Users.patientId;
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final Appointment APP = TestData.Appointments.appointment;
        APP.getAppointmentId().setShiftId(SHIFT_ID);
        APP.getShift().setId(SHIFT_ID);
        APP.getShift().getDoctor().setId(TestData.Users.doctorId);
        APP.getShift().getDoctor().getPicture().setId(TestData.Images.validImageId);
        APP.getPatient().setId(PATIENT_ID);
        APP.getPatient().getPicture().setId(TestData.Images.validImageId);
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2Id;
        final Appointment APP2 = TestData.Appointments.appointment2;
        APP2.getAppointmentId().setShiftId(SHIFT2_ID);
        APP2.getShift().setId(SHIFT2_ID);
        APP2.getShift().getDoctor().setId(TestData.Users.doctorId);
        APP2.getShift().getDoctor().getPicture().setId(TestData.Images.validImageId);
        APP2.getPatient().setId(PATIENT_ID);
        APP2.getPatient().getPicture().setId(TestData.Images.validImageId);
        final LocalDate APP_DATE_OLD = TestData.Appointments.oldAppointment.getDate();

        List<Appointment> results = appointmentDao.getAppointmentsForDate(APP_DATE);
        Appointment appFound1 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE));
        Appointment appFound2 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE));
        Appointment appFound3 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE_OLD));
        Appointment appFound4 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE_OLD));

        Assert.assertNotNull(results);
        Assert.assertNotNull(appFound1);
        Assert.assertNotNull(appFound2);
        Assert.assertNotNull(appFound3);
        Assert.assertNotNull(appFound4);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(APP));
        Assert.assertTrue(results.contains(APP2));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testRemoveAppointment(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2Id;
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final LocalDate APP_DATE_OLD = TestData.Appointments.oldAppointment.getDate();

        boolean result = appointmentDao.removeAppointment(SHIFT_ID, APP_DATE);
        Appointment appFound1 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE));
        Appointment appFound2 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE));
        Appointment appFound3 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE_OLD));
        Appointment appFound4 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE_OLD));

        Assert.assertTrue(result);
        Assert.assertNull(appFound1);
        Assert.assertNotNull(appFound2);
        Assert.assertNotNull(appFound3);
        Assert.assertNotNull(appFound4);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testRemoveAppointmentNonexistentAppointment(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2Id;
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final LocalDate APP_DATE_OLD = TestData.Appointments.oldAppointment.getDate();

        boolean result = appointmentDao.removeAppointment(99L, LocalDate.parse("1999-01-01"));
        Appointment appFound1 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE));
        Appointment appFound2 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE));
        Appointment appFound3 = em.find(Appointment.class, new AppointmentId(SHIFT_ID, APP_DATE_OLD));
        Appointment appFound4 = em.find(Appointment.class, new AppointmentId(SHIFT2_ID, APP_DATE_OLD));

        Assert.assertFalse(result);
        Assert.assertNotNull(appFound1);
        Assert.assertNotNull(appFound2);
        Assert.assertNotNull(appFound3);
        Assert.assertNotNull(appFound4);
    }
    
}
