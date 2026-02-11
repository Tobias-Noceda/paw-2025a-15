package ar.edu.itba.paw.persistence;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Before;
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

    @Before
    public void setup() {
        TestData.DoctorSingleShifts.doctorSingleShift.setId(TestData.DoctorSingleShifts.doctorSingleShiftId); // Ensure ID is set before creating APP
    }

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

        Optional<DoctorSingleShift> foundShift = doctorSingleShiftDao.getShiftById(SHIFT_ID);

        Assert.assertNotNull(foundShift);
        Assert.assertTrue(foundShift.isPresent());
        Assert.assertEquals(SHIFT, foundShift.get());
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
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void updateShiftsNoNewShiftsNull(){
        final Long DOC_ID = TestData.Users.doctorId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;

        doctorSingleShiftDao.updateShifts(DOC_ID, null);
        Doctor DOC = em.find(Doctor.class, DOC_ID);
    
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertTrue(DOC.getActiveSingleShifts().get(0).getIsActive());
        Assert.assertEquals(DOC_ID, DOC.getActiveSingleShifts().get(0).getDoctor().getId());
        Assert.assertEquals(OLD_SHIFT.getWeekday(), DOC.getActiveSingleShifts().get(0).getWeekday());
        Assert.assertEquals(OLD_SHIFT.getAddress(), DOC.getActiveSingleShifts().get(0).getAddress());
        Assert.assertEquals(OLD_SHIFT.getStartTime(), DOC.getActiveSingleShifts().get(0).getStartTime());
        Assert.assertEquals(OLD_SHIFT.getEndTime(), DOC.getActiveSingleShifts().get(0).getEndTime());
        Assert.assertEquals(OLD_SHIFT.getDuration(), DOC.getActiveSingleShifts().get(0).getDuration());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void updateShiftsNoNewShiftsEmpty(){
        final Long DOC_ID = TestData.Users.doctorId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;

        doctorSingleShiftDao.updateShifts(DOC_ID, Collections.emptyList());
        Doctor DOC = em.find(Doctor.class, DOC_ID);
    
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertTrue(DOC.getActiveSingleShifts().get(0).getIsActive());
        Assert.assertEquals(DOC_ID, DOC.getActiveSingleShifts().get(0).getDoctor().getId());
        Assert.assertEquals(OLD_SHIFT.getWeekday(), DOC.getActiveSingleShifts().get(0).getWeekday());
        Assert.assertEquals(OLD_SHIFT.getAddress(), DOC.getActiveSingleShifts().get(0).getAddress());
        Assert.assertEquals(OLD_SHIFT.getStartTime(), DOC.getActiveSingleShifts().get(0).getStartTime());
        Assert.assertEquals(OLD_SHIFT.getEndTime(), DOC.getActiveSingleShifts().get(0).getEndTime());
        Assert.assertEquals(OLD_SHIFT.getDuration(), DOC.getActiveSingleShifts().get(0).getDuration());
    }

    // @Test
    // @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:doctorNewSingleShifts.sql"})
    // public void updateShiftsNewShifts(){
    //     final Long DOC_ID = TestData.Users.doctorId;
    //     final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
    //     final DoctorSingleShift NEW_SHIFT = TestData.DoctorSingleShifts.newDoctorSingleShift;
    //     final Long NEW_SHIFT_ID = TestData.DoctorSingleShifts.newDoctorSingleShiftId;
    //     List<DoctorSingleShift> SHIFTS = List.of(OLD_SHIFT, NEW_SHIFT);

    //     doctorSingleShiftDao.updateShifts(DOC_ID, SHIFTS);
    //     Doctor DOC = em.find(Doctor.class, DOC_ID);
    //     DoctorSingleShift newShiftPersisted = em.find(DoctorSingleShift.class, NEW_SHIFT_ID);
    
    //     Assert.assertNotNull(DOC);
    //     Assert.assertEquals(2, DOC.getActiveSingleShifts().size());
    //     Assert.assertTrue(DOC.getActiveSingleShifts().get(0).getIsActive());
    //     Assert.assertTrue(DOC.getActiveSingleShifts().get(1).getIsActive());
    //     Assert.assertTrue(DOC.getActiveSingleShifts().contains(OLD_SHIFT));
    //     Assert.assertTrue(DOC.getActiveSingleShifts().contains(NEW_SHIFT));
    //     Assert.assertNotNull(newShiftPersisted);
    // }

}