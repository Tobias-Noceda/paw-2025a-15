package ar.edu.itba.paw.persistence;

import java.time.LocalTime;
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
    }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate (esta función ya no existe)
    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql"})
    // public void testBatchCreate() {
    //     final User DOC = TestData.Users.doctor;
    //     final long DOC_ID = TestData.Users.doctorId;
    //     DOC.setId(DOC_ID);
    //     DOC.getPicture().setId(TestData.Images.validImageId);
    //     final String ADDRESS = TestData.DoctorSingleShifts.doctorSingleShift.getAddress();
    //     final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
    //     final LocalTime START_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
    //     final LocalTime END_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();
    //     final DoctorSingleShift SHIFT1 = new DoctorSingleShift(DOC, WEEKDAY, ADDRESS, START_TIME, END_TIME);
    //     SHIFT1.setId(1L);
    //     final DoctorSingleShift SHIFT2 = new DoctorSingleShift(DOC, WEEKDAY, ADDRESS, START_TIME.plusMinutes(30), END_TIME.plusMinutes(30));
    //     SHIFT2.setId(2L);
    //     List<DoctorSingleShift> shifts = List.of(
    //         SHIFT1,
    //         SHIFT2
    //     );

    //     int[] results = doctorSingleShiftDao.batchCreate(shifts);
    //     DoctorSingleShift dsPersisted = em.find(DoctorSingleShift.class, SHIFT1.getId());
    //     DoctorSingleShift ds2Persisted = em.find(DoctorSingleShift.class, SHIFT2.getId());

    //     Assert.assertEquals(shifts.size(), results.length);
    //     for (int result : results) {
    //         Assert.assertTrue(result > 0);
    //     }
    //     Assert.assertNotNull(dsPersisted);
    //     Assert.assertNotNull(ds2Persisted);
    //     Assert.assertEquals(SHIFT1, dsPersisted);
    //     Assert.assertEquals(SHIFT2, ds2Persisted);
    // }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void getShiftById(){
        final Long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        SHIFT.setId(SHIFT_ID);
        SHIFT.getDoctor().setId(TestData.Users.doctorId);
        SHIFT.getDoctor().getPicture().setId(TestData.Images.validImageId);

        Optional<DoctorSingleShift> foundShift = doctorSingleShiftDao.getShiftById(SHIFT_ID);

        Assert.assertNotNull(foundShift);
        Assert.assertTrue(foundShift.isPresent());
        Assert.assertEquals(SHIFT, foundShift.get());
    }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate (esta función ya no existe)
    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    // public void testGetShiftsByDoctorId(){
    //     final long DOC_ID = TestData.Users.doctorId;
    //     final DoctorSingleShift SHIFT1 = TestData.DoctorSingleShifts.doctorSingleShift;
    //     SHIFT1.setId(TestData.DoctorSingleShifts.doctorSingleShiftId);
    //     SHIFT1.getDoctor().setId(DOC_ID);
    //     SHIFT1.getDoctor().getPicture().setId(TestData.Images.validImageId);
    //     final DoctorSingleShift SHIFT2 = TestData.DoctorSingleShifts.doctorSingleShift2;
    //     SHIFT2.setId(TestData.DoctorSingleShifts.doctorSingleShift2Id);
    //     SHIFT2.getDoctor().setId(DOC_ID);
    //     SHIFT2.getDoctor().getPicture().setId(TestData.Images.validImageId);

    //     List<DoctorSingleShift> foundShifts = doctorSingleShiftDao.getShiftsByDoctorId(DOC_ID);

    //     Assert.assertFalse(foundShifts.isEmpty());
    //     Assert.assertEquals(2, foundShifts.size());
    //     Assert.assertTrue(foundShifts.contains(SHIFT1));
    //     Assert.assertTrue(foundShifts.contains(SHIFT2));
    // }

    // TODO: revisar, esto no parece tener más sentido gracias a hibernate (esta función ya no existe)
    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql"})
    // public void testGetShiftsByDoctorIdNonexistentShiftsDoc(){
    //     final long DOC_ID = TestData.Users.doctorId;

    //     List<DoctorSingleShift> foundShifts = doctorSingleShiftDao.getShiftsByDoctorId(DOC_ID);

    //     Assert.assertTrue(foundShifts.isEmpty());
    // }

}