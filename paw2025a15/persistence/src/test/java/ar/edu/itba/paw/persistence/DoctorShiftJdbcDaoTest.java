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

import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;


@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DoctorShiftJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private DoctorShiftJdbcDao doctorShiftDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }    

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testCreate(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();

        DoctorShift doctorShift = doctorShiftDao.create(DOC_ID, WEEKDAY, ADDRESS, START_TIME, END_TIME);

        Assert.assertNotNull(doctorShift);
        Assert.assertEquals(DOC_ID, doctorShift.getDoctorId());
        Assert.assertEquals(WEEKDAY, doctorShift.getWeekday());
        Assert.assertEquals(ADDRESS, doctorShift.getAddress());
        Assert.assertEquals(START_TIME, doctorShift.getStartTime());
        Assert.assertEquals(END_TIME, doctorShift.getEndTime());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void testCreateExistentShift(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();

        Assert.assertThrows(DataIntegrityViolationException.class, () -> {
            doctorShiftDao.create(DOC_ID, WEEKDAY, ADDRESS, START_TIME, END_TIME);
        });
    }

    @Test
    public void testCreateNonexistentDoc(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();

        Assert.assertThrows(DataIntegrityViolationException.class, () -> {
            doctorShiftDao.create(DOC_ID, WEEKDAY, ADDRESS, START_TIME, END_TIME);
        });
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void getShiftById(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShift.getId();
        final DoctorShift SHIFT = TestData.DoctorShifts.doctorShift;

        Optional<DoctorShift>  foundShift = doctorShiftDao.getShiftById(SHIFT_ID);

        Assert.assertTrue(foundShift.isPresent());
        Assert.assertEquals(SHIFT, foundShift.get());
        Assert.assertEquals(1, 
                            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("shift_id = %d", SHIFT_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void getShiftByIdNonexistentShift(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShift.getId();

        Optional<DoctorShift>  foundShift = doctorShiftDao.getShiftById(SHIFT_ID);

        Assert.assertFalse(foundShift.isPresent());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void testGetShiftsByDoctorId(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final DoctorShift SHIFT1 = TestData.DoctorShifts.doctorShift;
        final DoctorShift SHIFT2 = TestData.DoctorShifts.doctorShift2;

        List<DoctorShift> foundShifts = doctorShiftDao.getShiftsByDoctorId(DOC_ID);

        Assert.assertFalse(foundShifts.isEmpty());
        Assert.assertEquals(2, foundShifts.size());
        Assert.assertTrue(foundShifts.contains(SHIFT1));
        Assert.assertTrue(foundShifts.contains(SHIFT2));
    }

    @Test
    public void testGetShiftsByDoctorIdNonexistentDoc(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();

        List<DoctorShift> foundShifts = doctorShiftDao.getShiftsByDoctorId(DOC_ID);

        Assert.assertTrue(foundShifts.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetShiftsByDoctorIdNonexistentShiftsDoc(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();

        List<DoctorShift> foundShifts = doctorShiftDao.getShiftsByDoctorId(DOC_ID);

        Assert.assertTrue(foundShifts.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateNoAppointments(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalDate DATE = TestData.Appointments.appointment.getDate();
        final DoctorShift SHIFT1 = TestData.DoctorShifts.doctorShift;
        final DoctorShift SHIFT2 = TestData.DoctorShifts.doctorShift2;

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDate(DOC_ID, WEEKDAY, DATE);

        Assert.assertFalse(foundShifts.isEmpty());
        Assert.assertEquals(2, foundShifts.size());
        Assert.assertTrue(foundShifts.contains(SHIFT1));
        Assert.assertTrue(foundShifts.contains(SHIFT2));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateAllAppointments(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalDate DATE = TestData.Appointments.appointment.getDate();

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDate(DOC_ID, WEEKDAY, DATE);

        Assert.assertTrue(foundShifts.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateNonexistentDoc(){
        final long DOC_ID = TestData.Users.patient.getId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalDate DATE = TestData.Appointments.appointment.getDate();

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDate(DOC_ID, WEEKDAY, DATE);

        Assert.assertTrue(foundShifts.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateTimeNoAppointments(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalDate DATE = TestData.Appointments.appointment.getDate();
        final LocalTime TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final DoctorShift SHIFT2 = TestData.DoctorShifts.doctorShift2;

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDateTime(DOC_ID, WEEKDAY, DATE, TIME);

        Assert.assertFalse(foundShifts.isEmpty());
        Assert.assertEquals(1, foundShifts.size());
        Assert.assertTrue(foundShifts.contains(SHIFT2));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateTimeAllAppointments(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalDate DATE = TestData.Appointments.appointment.getDate();
        final LocalTime TIME = TestData.DoctorShifts.doctorShift.getStartTime();

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDateTime(DOC_ID, WEEKDAY, DATE, TIME);

        Assert.assertTrue(foundShifts.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateTimeNonexistentDoc(){
        final long DOC_ID = TestData.Users.patient.getId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalDate DATE = TestData.Appointments.appointment.getDate();
        final LocalTime TIME = TestData.DoctorShifts.doctorShift.getStartTime();

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDateTime(DOC_ID, WEEKDAY, DATE, TIME);

        Assert.assertTrue(foundShifts.isEmpty());
    }

}
