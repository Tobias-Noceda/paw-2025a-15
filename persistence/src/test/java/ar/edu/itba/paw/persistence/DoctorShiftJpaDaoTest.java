package ar.edu.itba.paw.persistence;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
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
        final Long OLD_SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        OLD_SHIFT.setId(OLD_SHIFT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        OLD_SHIFT.getDoctor().setId(DOC_ID);

        doctorSingleShiftDao.updateShifts(DOC_ID, null);
        Doctor DOC = em.find(Doctor.class, DOC_ID);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, OLD_SHIFT_ID);
    
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertNotNull(shiftPersisted);
        Assert.assertTrue(shiftPersisted.getIsActive());
        Assert.assertTrue(DOC.getActiveSingleShifts().contains(OLD_SHIFT));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void updateShiftsNoNewShiftsEmpty(){
        final Long OLD_SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        OLD_SHIFT.setId(OLD_SHIFT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        OLD_SHIFT.getDoctor().setId(DOC_ID);

        doctorSingleShiftDao.updateShifts(DOC_ID, Collections.emptyList());
        Doctor DOC = em.find(Doctor.class, DOC_ID);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, OLD_SHIFT_ID);
    
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertNotNull(shiftPersisted);
        Assert.assertTrue(shiftPersisted.getIsActive());
        Assert.assertTrue(DOC.getActiveSingleShifts().contains(OLD_SHIFT));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetActiveShiftsByDoctorId() {
        final Long OLD_SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        OLD_SHIFT.setId(OLD_SHIFT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        OLD_SHIFT.getDoctor().setId(DOC_ID);

        List<DoctorSingleShift> shifts = doctorSingleShiftDao.getActiveShiftsByDoctorId(DOC_ID);
        Doctor DOC = em.find(Doctor.class, DOC_ID);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, OLD_SHIFT_ID);

        Assert.assertNotNull(shifts);
        Assert.assertFalse(shifts.isEmpty());
        Assert.assertNotNull(shiftPersisted);
        Assert.assertTrue(shiftPersisted.getIsActive());
        Assert.assertEquals(DOC_ID, shifts.get(0).getDoctor().getId());
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertTrue(DOC.getActiveSingleShifts().contains(OLD_SHIFT));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetActiveShiftsByDoctorIdPage() {
        final Long OLD_SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        OLD_SHIFT.setId(OLD_SHIFT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        OLD_SHIFT.getDoctor().setId(DOC_ID);

        List<DoctorSingleShift> shifts = doctorSingleShiftDao.getActiveShiftsByDoctorIdPage(DOC_ID, 1, 100);
        Doctor DOC = em.find(Doctor.class, DOC_ID);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, OLD_SHIFT_ID);

        Assert.assertNotNull(shifts);
        Assert.assertFalse(shifts.isEmpty());
        Assert.assertNotNull(shiftPersisted);
        Assert.assertTrue(shiftPersisted.getIsActive());
        Assert.assertEquals(DOC_ID, shifts.get(0).getDoctor().getId());
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertTrue(DOC.getActiveSingleShifts().contains(OLD_SHIFT));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:doctorInactiveShift.sql"})
    public void testGetActiveShiftsByDoctorIdPageAllInactive() {
        final Long OLD_SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final Long INACTIVE_SHIFT_ID = TestData.DoctorSingleShifts.doctorInactiveSingleShiftId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        OLD_SHIFT.setId(OLD_SHIFT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        OLD_SHIFT.getDoctor().setId(DOC_ID);

        List<DoctorSingleShift> shifts = doctorSingleShiftDao.getActiveShiftsByDoctorIdPage(DOC_ID, 1, 100);
        Doctor DOC = em.find(Doctor.class, DOC_ID);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, OLD_SHIFT_ID);
        DoctorSingleShift shiftInactivePersisted = em.find(DoctorSingleShift.class, INACTIVE_SHIFT_ID);

        Assert.assertNotNull(shifts);
        Assert.assertFalse(shifts.isEmpty());
        Assert.assertNotNull(shiftPersisted);
        Assert.assertNotNull(shiftInactivePersisted);
        Assert.assertTrue(shiftPersisted.getIsActive());
        Assert.assertEquals(DOC_ID, shifts.get(0).getDoctor().getId());
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertTrue(DOC.getActiveSingleShifts().contains(OLD_SHIFT));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetActiveShiftsByDoctorIdPageNullDoc() {
        final Long DOC_ID = 0L;

        List<DoctorSingleShift> shifts = doctorSingleShiftDao.getActiveShiftsByDoctorIdPage(DOC_ID, 1, 100);

        Assert.assertNotNull(shifts);
        Assert.assertTrue(shifts.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetActiveShiftsByDoctorIdPageInvalidPage() {
        final Long DOC_ID = TestData.Users.doctorId;

        List<DoctorSingleShift> shifts = doctorSingleShiftDao.getActiveShiftsByDoctorIdPage(DOC_ID, 0, 100);
        Doctor DOC = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(shifts);
        Assert.assertTrue(shifts.isEmpty());
        Assert.assertNotNull(DOC);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetActiveShiftsByDoctorIdPageInvalidPageSize() {
        final Long DOC_ID = TestData.Users.doctorId;

        List<DoctorSingleShift> shifts = doctorSingleShiftDao.getActiveShiftsByDoctorIdPage(DOC_ID, 1, 0);
        Doctor DOC = em.find(Doctor.class, DOC_ID);

        Assert.assertNotNull(shifts);
        Assert.assertTrue(shifts.isEmpty());
        Assert.assertNotNull(DOC);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetActiveShiftsByDoctorIdCount() {
        final Long OLD_SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        OLD_SHIFT.setId(OLD_SHIFT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        OLD_SHIFT.getDoctor().setId(DOC_ID);

        int shifts = doctorSingleShiftDao.getActiveShiftsByDoctorIdCount(DOC_ID);
        Doctor DOC = em.find(Doctor.class, DOC_ID);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, OLD_SHIFT_ID);

        Assert.assertEquals(1, shifts);
        Assert.assertNotNull(shiftPersisted);
        Assert.assertTrue(shiftPersisted.getIsActive());
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertTrue(DOC.getActiveSingleShifts().contains(OLD_SHIFT));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:doctorInactiveShift.sql"})
    public void testGetActiveShiftsByDoctorIdCountAllInactive() {
        final Long OLD_SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final Long INACTIVE_SHIFT_ID = TestData.DoctorSingleShifts.doctorInactiveSingleShiftId;
        final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        OLD_SHIFT.setId(OLD_SHIFT_ID);
        final Long DOC_ID = TestData.Users.doctorId;
        OLD_SHIFT.getDoctor().setId(DOC_ID);

        int shifts = doctorSingleShiftDao.getActiveShiftsByDoctorIdCount(DOC_ID);
        Doctor DOC = em.find(Doctor.class, DOC_ID);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, OLD_SHIFT_ID);
        DoctorSingleShift shiftInactivePersisted = em.find(DoctorSingleShift.class, INACTIVE_SHIFT_ID);

        Assert.assertEquals(1, shifts);
        Assert.assertNotNull(shiftPersisted);
        Assert.assertNotNull(shiftInactivePersisted);
        Assert.assertTrue(shiftPersisted.getIsActive());
        Assert.assertNotNull(DOC);
        Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
        Assert.assertTrue(DOC.getActiveSingleShifts().contains(OLD_SHIFT));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
    public void testGetActiveShiftsByDoctorIdCountNullDoc() {
        final Long DOC_ID = 0L;

        int shifts = doctorSingleShiftDao.getActiveShiftsByDoctorIdCount(DOC_ID);

        Assert.assertEquals(0, shifts);
    }
    
    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testDoctorSetShifts(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        final Long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        List<DoctorSingleShift> SHIFTS = List.of(SHIFT);

        doctorSingleShiftDao.doctorSetShifts(DOC, SHIFTS);
        Doctor persistedDoctor = em.find(Doctor.class, DOC_ID);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, SHIFT_ID);
    
        Assert.assertNotNull(persistedDoctor);
        Assert.assertEquals(1, persistedDoctor.getActiveSingleShifts().size());
        Assert.assertNotNull(shiftPersisted);
        Assert.assertTrue(shiftPersisted.getIsActive());
        Assert.assertTrue(persistedDoctor.getActiveSingleShifts().contains(SHIFT));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testDoctorSetShiftsNullDoc(){
        final Doctor DOC = null;
        final Long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
        final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
        List<DoctorSingleShift> SHIFTS = List.of(SHIFT);

        doctorSingleShiftDao.doctorSetShifts(DOC, SHIFTS);
        DoctorSingleShift shiftPersisted = em.find(DoctorSingleShift.class, SHIFT_ID);
    
        Assert.assertNull(shiftPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testDoctorSetShiftsNullShifts(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        List<DoctorSingleShift> SHIFTS = null;

        doctorSingleShiftDao.doctorSetShifts(DOC, SHIFTS);
        Doctor persistedDoctor = em.find(Doctor.class, DOC_ID);
    
        Assert.assertNotNull(persistedDoctor);
        Assert.assertEquals(0, persistedDoctor.getActiveSingleShifts().size());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql"})
    public void testDoctorSetShiftsEmptyShifts(){
        final Long DOC_ID = TestData.Users.doctorId;
        final Doctor DOC = TestData.Users.doctor;
        DOC.setId(DOC_ID);
        List<DoctorSingleShift> SHIFTS = List.of();

        doctorSingleShiftDao.doctorSetShifts(DOC, SHIFTS);
        Doctor persistedDoctor = em.find(Doctor.class, DOC_ID);
    
        Assert.assertNotNull(persistedDoctor);
        Assert.assertEquals(0, persistedDoctor.getActiveSingleShifts().size());
    }

}