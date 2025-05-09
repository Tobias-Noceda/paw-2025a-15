package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AppointmentsJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private AppointmentJdbcDao appointmentDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void testAddAppointment(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShift.getId();
        final long PATIENT_ID = TestData.Users.patient.getId();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final Appointment APP = TestData.Appointments.appointment;

        Appointment appointment = appointmentDao.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE);

        Assert.assertNotNull(appointment);
        Assert.assertEquals(APP, appointment);
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d AND patient_id = %d AND appointment_date = '%s'", SHIFT_ID, PATIENT_ID, java.sql.Date.valueOf(APP_DATE))));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testAddAppointmentNonexistentShift(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShift.getId();
        final long PATIENT_ID = TestData.Users.patient.getId();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();

        Assert.assertThrows(DataIntegrityViolationException.class,()->{
            appointmentDao.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE);}
        );
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql"})
    public void testGetAppointmentsByShiftIdAndDate(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShift.getId();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final Appointment APP = TestData.Appointments.appointment;

        Optional<Appointment> appointment = appointmentDao.getAppointmentsByShiftIdAndDate(SHIFT_ID, APP_DATE);

        Assert.assertTrue(appointment.isPresent());
        Assert.assertEquals(APP, appointment.get());
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d AND appointment_date = '%s'", SHIFT_ID, java.sql.Date.valueOf(APP_DATE))));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testGetFutureAppointmentDataByPatientId(){
        final long PATIENT_ID = TestData.Users.patient.getId();
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final long DOCTOR_ID = TestData.Users.doctor.getId();
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long SHIFT_ID = TestData.DoctorShifts.doctorShift.getId();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2.getId();
        final LocalTime START_TIME2 = TestData.DoctorShifts.doctorShift2.getStartTime();
        final LocalTime END_TIME2 = TestData.DoctorShifts.doctorShift2.getEndTime();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final AppointmentData APP_DATA = new AppointmentData(SHIFT_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME, END_TIME, ADDRESS);
        final AppointmentData APP_DATA2 = new AppointmentData(SHIFT2_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME2, END_TIME2, ADDRESS);

         List<AppointmentData> results = appointmentDao.getFutureAppointmentDataByPatientId(PATIENT_ID);

        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(APP_DATA));
        Assert.assertTrue(results.contains(APP_DATA2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("patient_id = %d AND appointment_date = '%s'", PATIENT_ID, java.sql.Date.valueOf(APP_DATE))));
        Assert.assertEquals(4, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("patient_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testGetOldAppointmentDataByPatientId(){
        final long PATIENT_ID = TestData.Users.patient.getId();
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final long DOCTOR_ID = TestData.Users.doctor.getId();
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long SHIFT_ID = TestData.DoctorShifts.doctorShift.getId();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2.getId();
        final LocalTime START_TIME2 = TestData.DoctorShifts.doctorShift2.getStartTime();
        final LocalTime END_TIME2 = TestData.DoctorShifts.doctorShift2.getEndTime();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalDate APP_DATE = TestData.Appointments.oldAppointment.getDate();
        final AppointmentData APP_DATA = new AppointmentData(SHIFT_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME, END_TIME, ADDRESS);
        final AppointmentData APP_DATA2 = new AppointmentData(SHIFT2_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME2, END_TIME2, ADDRESS);

         List<AppointmentData> results = appointmentDao.getOldAppointmentDataByPatientId(PATIENT_ID);

        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(APP_DATA));
        Assert.assertTrue(results.contains(APP_DATA2));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("patient_id = %d AND appointment_date = '%s'", PATIENT_ID, java.sql.Date.valueOf(APP_DATE))));
        Assert.assertEquals(4, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("patient_id = %d", PATIENT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testGetFutureAppointmentDataByDoctorId(){
        final long PATIENT_ID = TestData.Users.patient.getId();
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final long DOCTOR_ID = TestData.Users.doctor.getId();
        final String DOC_NAME = TestData.Users.doctor.getName();
        final long SHIFT_ID = TestData.DoctorShifts.doctorShift.getId();
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();
        final long SHIFT2_ID = TestData.DoctorShifts.doctorShift2.getId();
        final LocalTime START_TIME2 = TestData.DoctorShifts.doctorShift2.getStartTime();
        final LocalTime END_TIME2 = TestData.DoctorShifts.doctorShift2.getEndTime();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();
        final AppointmentData APP_DATA = new AppointmentData(SHIFT_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME, END_TIME, ADDRESS);
        final AppointmentData APP_DATA2 = new AppointmentData(SHIFT2_ID, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOC_NAME, APP_DATE, START_TIME2, END_TIME2, ADDRESS);

         List<AppointmentData> results = appointmentDao.getFutureAppointmentDataByDoctorId(DOC_ID);

        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(APP_DATA));
        Assert.assertTrue(results.contains(APP_DATA2));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d AND appointment_date = '%s'", SHIFT_ID, java.sql.Date.valueOf(APP_DATE))));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d AND appointment_date = '%s'", SHIFT2_ID, java.sql.Date.valueOf(APP_DATE))));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d", SHIFT_ID)));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d", SHIFT2_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testGetAppointmentsForDate(){
        final Appointment APP = TestData.Appointments.appointment;
        final long SHIFT_ID = TestData.Appointments.appointment.getShiftId();
        final Appointment APP2 = TestData.Appointments.appointment2;
        final long SHIFT2_ID = TestData.Appointments.appointment2.getShiftId();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();

        List<Appointment> results = appointmentDao.getAppointmentsForDate(APP_DATE);

        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(APP));
        Assert.assertTrue(results.contains(APP2));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d AND appointment_date = '%s'", SHIFT_ID, java.sql.Date.valueOf(APP_DATE))));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d AND appointment_date = '%s'", SHIFT2_ID, java.sql.Date.valueOf(APP_DATE))));
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("appointment_date = '%s'", java.sql.Date.valueOf(APP_DATE)))); 
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testRemoveAppointment(){
        final long SHIFT_ID = TestData.Appointments.appointment.getShiftId();
        final long SHIFT2_ID = TestData.Appointments.appointment2.getShiftId();
        final LocalDate APP_DATE = TestData.Appointments.appointment.getDate();

        boolean result = appointmentDao.removeAppointment(SHIFT_ID, APP_DATE);

        Assert.assertTrue(result);
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d AND appointment_date = '%s'", SHIFT_ID, java.sql.Date.valueOf(APP_DATE))));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d AND appointment_date = '%s'", SHIFT2_ID, java.sql.Date.valueOf(APP_DATE))));
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("appointment_date = '%s'", java.sql.Date.valueOf(APP_DATE)))); 
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql", "classpath:oldAppointments.sql"})
    public void testRemoveAppointmentNonexistentAppointment(){

        boolean result = appointmentDao.removeAppointment(99L, LocalDate.parse("1999-01-01"));

        Assert.assertFalse(result);
        Assert.assertEquals(4, 
        JdbcTestUtils.countRowsInTable(jdbcTemplate, "appointments")); 
    }

}
