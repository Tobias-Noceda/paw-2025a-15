package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
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

import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;


@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DoctorShiftJpaDaoTest {
    
    @Autowired
    private DoctorShiftJpaDao doctorShiftDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testCreate(){
        final User DOC =  TestData.Users.doctor;
        final long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();

        DoctorShift doctorShift = doctorShiftDao.create(DOC, WEEKDAY, ADDRESS, START_TIME, END_TIME);
        DoctorShift dsPersisted = em.find(DoctorShift.class, doctorShift.getId());

        Assert.assertNotNull(dsPersisted);
        Assert.assertEquals(DOC_ID, dsPersisted.getDoctor().getId());
        Assert.assertEquals(WEEKDAY, dsPersisted.getWeekday());
        Assert.assertEquals(ADDRESS, dsPersisted.getAddress());
        Assert.assertEquals(START_TIME, dsPersisted.getStartTime());
        Assert.assertEquals(END_TIME, dsPersisted.getEndTime());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testBatchCreate() {
        final User DOC = TestData.Users.doctor;
        final long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        DOC.getPicture().setId(TestData.Images.validImageId);
        final String ADDRESS = TestData.DoctorShifts.doctorShift.getAddress();
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalTime START_TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorShifts.doctorShift.getEndTime();
        final DoctorShift SHIFT1 = new DoctorShift(DOC, WEEKDAY, ADDRESS, START_TIME, END_TIME);
        SHIFT1.setId(1L);
        final DoctorShift SHIFT2 = new DoctorShift(DOC, WEEKDAY, ADDRESS, START_TIME.plusMinutes(30), END_TIME.plusMinutes(30));
        SHIFT2.setId(2L);
        List<DoctorShift> shifts = List.of(
            SHIFT1,
            SHIFT2
        );

        int[] results = doctorShiftDao.batchCreate(shifts);
        DoctorShift dsPersisted = em.find(DoctorShift.class, SHIFT1.getId());
        DoctorShift ds2Persisted = em.find(DoctorShift.class, SHIFT2.getId());

        Assert.assertEquals(shifts.size(), results.length);
        for (int result : results) {
            Assert.assertTrue(result > 0);
        }
        Assert.assertNotNull(dsPersisted);
        Assert.assertNotNull(ds2Persisted);
        Assert.assertEquals(SHIFT1, dsPersisted);
        Assert.assertEquals(SHIFT2, ds2Persisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void getShiftById(){
        final long SHIFT_ID = TestData.DoctorShifts.doctorShiftId;
        final DoctorShift SHIFT = TestData.DoctorShifts.doctorShift;
        SHIFT.setId(SHIFT_ID);
        SHIFT.getDoctor().setId(TestData.Users.doctorId);
        SHIFT.getDoctor().getPicture().setId(TestData.Images.validImageId);

        Optional<DoctorShift> foundShift = doctorShiftDao.getShiftById(SHIFT_ID);

        Assert.assertNotNull(foundShift);
        Assert.assertTrue(foundShift.isPresent());
        Assert.assertEquals(SHIFT, foundShift.get());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void testGetShiftsByDoctorId(){
        final long DOC_ID = TestData.Users.doctorId;
        final DoctorShift SHIFT1 = TestData.DoctorShifts.doctorShift;
        SHIFT1.setId(TestData.DoctorShifts.doctorShiftId);
        SHIFT1.getDoctor().setId(DOC_ID);
        SHIFT1.getDoctor().getPicture().setId(TestData.Images.validImageId);
        final DoctorShift SHIFT2 = TestData.DoctorShifts.doctorShift2;
        SHIFT2.setId(TestData.DoctorShifts.doctorShift2Id);
        SHIFT2.getDoctor().setId(DOC_ID);
        SHIFT2.getDoctor().getPicture().setId(TestData.Images.validImageId);

        List<DoctorShift> foundShifts = doctorShiftDao.getShiftsByDoctorId(DOC_ID);

        Assert.assertFalse(foundShifts.isEmpty());
        Assert.assertEquals(2, foundShifts.size());
        Assert.assertTrue(foundShifts.contains(SHIFT1));
        Assert.assertTrue(foundShifts.contains(SHIFT2));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetShiftsByDoctorIdNonexistentShiftsDoc(){
        final long DOC_ID = TestData.Users.doctorId;

        List<DoctorShift> foundShifts = doctorShiftDao.getShiftsByDoctorId(DOC_ID);

        Assert.assertTrue(foundShifts.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateTimeNoAppointments(){
        final long DOC_ID = TestData.Users.doctorId;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalDate DATE = TestData.Appointments.appointment.getDate();
        final LocalTime TIME = TestData.DoctorShifts.doctorShift.getStartTime();
        final DoctorShift SHIFT2 = TestData.DoctorShifts.doctorShift2;
        SHIFT2.setId(TestData.DoctorShifts.doctorShift2Id);
        SHIFT2.getDoctor().setId(DOC_ID);
        SHIFT2.getDoctor().getPicture().setId(TestData.Images.validImageId);

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDateTime(DOC_ID, WEEKDAY, DATE, TIME);

        Assert.assertFalse(foundShifts.isEmpty());
        Assert.assertEquals(1, foundShifts.size());
        Assert.assertTrue(foundShifts.contains(SHIFT2));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorShifts.sql", "classpath:appointments.sql"})
    public void testGetAvailableShiftsByDoctorIdWeekdayAndDateTimeAllAppointments(){
        final long DOC_ID = TestData.Users.doctorId;
        final WeekdayEnum WEEKDAY = TestData.DoctorShifts.doctorShift.getWeekday();
        final LocalDate DATE = TestData.Appointments.appointment.getDate();
        final LocalTime TIME = TestData.DoctorShifts.doctorShift.getStartTime();

        List<DoctorShift> foundShifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDateTime(DOC_ID, WEEKDAY, DATE, TIME);

        Assert.assertTrue(foundShifts.isEmpty());
    }
}
