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

import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DoctorShiftJpaDaoTest {
    
    @Autowired
    private DoctorShiftJpaDao doctorSingleShiftDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testCreate(){
        final Doctor DOC =  TestData.Users.doctor;
        final Long DOC_ID = TestData.Users.doctorId;
        DOC.setId(DOC_ID);
        final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
        final String ADDRESS = TestData.DoctorSingleShifts.doctorSingleShift.getAddress();
        final LocalTime START_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
        final LocalTime END_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();
        final int SLOT = TestData.DoctorSingleShifts.doctorSingleShift.getDuration();

        DoctorSingleShift doctorSingleShift = doctorSingleShiftDao.create(DOC, WEEKDAY, ADDRESS, START_TIME, END_TIME, SLOT);
        DoctorSingleShift dsPersisted = em.find(DoctorSingleShift.class, doctorSingleShift.getId());

        Assert.assertNotNull(dsPersisted);
        Assert.assertEquals(DOC_ID, dsPersisted.getDoctor().getId());
        Assert.assertEquals(WEEKDAY, dsPersisted.getWeekday());
        Assert.assertEquals(ADDRESS, dsPersisted.getAddress());
        Assert.assertEquals(START_TIME, dsPersisted.getStartTime());
        Assert.assertEquals(END_TIME, dsPersisted.getEndTime());
        Assert.assertEquals(SLOT, dsPersisted.getDuration());
        Assert.assertTrue(dsPersisted.getIsActive());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetShiftById(){
        final Long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        SHIFT.setId(SHIFT_ID);
        SHIFT.getDoctor().setId(TestData.Users.doctorId);
        SHIFT.getDoctor().getPicture().setId(TestData.Images.validImageId);

        Optional<DoctorSingleShift> foundShift = doctorSingleShiftDao.getShiftById(SHIFT_ID);

        Assert.assertNotNull(foundShift);
        Assert.assertTrue(foundShift.isPresent());
        Assert.assertEquals(SHIFT, foundShift.get());
        Assert.assertEquals(SHIFT.getDoctor().getId(), foundShift.get().getDoctor().getId());
        Assert.assertEquals(SHIFT.getWeekday(), foundShift.get().getWeekday());
        Assert.assertEquals(SHIFT.getAddress(), foundShift.get().getAddress());
        Assert.assertEquals(SHIFT.getStartTime(), foundShift.get().getStartTime());
        Assert.assertEquals(SHIFT.getEndTime(), foundShift.get().getEndTime());
        Assert.assertEquals(SHIFT.getDuration(), foundShift.get().getDuration());
        Assert.assertEquals(SHIFT.getIsActive(), foundShift.get().getIsActive());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetShiftByIdNonexistentShift(){
        final Long SHIFT_ID = 0L;

        Optional<DoctorSingleShift> foundShift = doctorSingleShiftDao.getShiftById(SHIFT_ID);

        Assert.assertNotNull(foundShift);
        Assert.assertFalse(foundShift.isPresent());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testDoctorSetShifts(){
        final Long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        SHIFT.setId(SHIFT_ID);
        SHIFT.getDoctor().setId(TestData.Users.doctorId);
        SHIFT.getDoctor().getPicture().setId(TestData.Images.validImageId);
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = em.find(Doctor.class, DOC_ID);

        doctorSingleShiftDao.doctorSetShifts(DOC, List.of(SHIFT));

        Assert.assertEquals(1, DOC.getSingleShifts().size());
        Assert.assertEquals(SHIFT.getId(), DOC.getSingleShifts().get(0).getId());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetAvailableTurnsByDoctorByDateNullDoc(){

        List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(null, LocalDate.now());

        Assert.assertTrue(avTurns.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetAvailableTurnsByDoctorByDateNullDate(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = em.find(Doctor.class, DOC_ID);

        List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(DOC, null);

        Assert.assertTrue(avTurns.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetAvailableTurnsByDoctorByDateBeforeDate(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = em.find(Doctor.class, DOC_ID);

        List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(DOC, LocalDate.now().minusDays(1));

        Assert.assertTrue(avTurns.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testGetAvailableTurnsByDoctorByDateNoDocShifts(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = em.find(Doctor.class, DOC_ID);

        List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(DOC, LocalDate.now().plusDays(1));

        Assert.assertTrue(avTurns.isEmpty());
    }

}