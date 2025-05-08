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
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testBatchCreate() {
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();
        List<DoctorShift> shifts = List.of(
            new DoctorShift(0, DOC_ID, WEEKDAY, ADDRESS, START_TIME, END_TIME),
            new DoctorShift(0, DOC_ID, WEEKDAY, ADDRESS, START_TIME.plusMinutes(30), END_TIME.plusMinutes(30))
        );

        int[] results = doctorShiftDao.batchCreate(shifts);

        Assert.assertEquals(shifts.size(), results.length);
        for (int result : results) {
            Assert.assertTrue(result > 0);
        }
        List<DoctorShift> retrievedShifts = doctorShiftDao.getShiftsByDoctorId(DOC_ID);
        Assert.assertEquals(shifts.size(), retrievedShifts.size());
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id = %d AND shift_weekday = %d AND shift_address LIKE '%s'", DOC_ID, WEEKDAY.ordinal(), ADDRESS)));
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
        Assert.assertEquals(2, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id = %d ", DOC_ID)));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetShiftsByDoctorIdNonexistentShiftsDoc(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();

        List<DoctorShift> foundShifts = doctorShiftDao.getShiftsByDoctorId(DOC_ID);

        Assert.assertTrue(foundShifts.isEmpty());
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "doctor_shifts", String.format("doctor_id = %d ", DOC_ID)));
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
        Assert.assertEquals(0, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d ", SHIFT2.getId())));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateTimeAllAppointments(){
        final long DOC_ID = TestData.DoctorShifts.doctorShift.getDoctorId();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final DoctorShift SHIFT2 = TestData.DoctorShifts.doctorShift;
        final LocalDate DATE = TestData.Appointments.appointment.getDate();
        final LocalTime TIME = TestData.DoctorShifts.doctorShift.getStartTime();

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDateTime(DOC_ID, WEEKDAY, DATE, TIME);

        Assert.assertTrue(foundShifts.isEmpty());
        Assert.assertEquals(1, 
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", String.format("shift_id = %d ", SHIFT2.getId())));
    }

}
