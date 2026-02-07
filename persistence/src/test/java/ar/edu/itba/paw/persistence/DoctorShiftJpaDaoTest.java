// package ar.edu.itba.paw.persistence;

// import java.time.LocalTime;
// import java.util.Collections;
// import java.util.Optional;

// import javax.persistence.EntityManager;
// import javax.persistence.PersistenceContext;

// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.test.annotation.Rollback;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.jdbc.Sql;
// import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
// import org.springframework.transaction.annotation.Transactional;

// import ar.edu.itba.paw.models.entities.Doctor;
// import ar.edu.itba.paw.models.entities.DoctorSingleShift;
// import ar.edu.itba.paw.models.enums.WeekdayEnum;
// import ar.edu.itba.paw.persistence.config.TestConfig;

// @Transactional
// @Rollback
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = TestConfig.class)
// public class DoctorShiftJpaDaoTest {
    
//     @Autowired
//     private DoctorShiftJpaDao doctorSingleShiftDao;

//     @PersistenceContext
//     private EntityManager em;

//     @Before
//     public void setup() {
//         TestData.DoctorSingleShifts.doctorSingleShift.setId(TestData.DoctorSingleShifts.doctorSingleShiftId); // Ensure ID is set before creating APP
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql"})
//     public void testCreate(){
//         final Doctor DOC =  TestData.Users.doctor;
//         final Long DOC_ID = TestData.Users.doctorId;
//         DOC.setId(DOC_ID);
//         final WeekdayEnum WEEKDAY = TestData.DoctorSingleShifts.doctorSingleShift.getWeekday();
//         final String ADDRESS = TestData.DoctorSingleShifts.doctorSingleShift.getAddress();
//         final LocalTime START_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime END_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();
//         final int SLOT = TestData.DoctorSingleShifts.doctorSingleShift.getDuration();

//         DoctorSingleShift doctorSingleShift = doctorSingleShiftDao.create(DOC, WEEKDAY, ADDRESS, START_TIME, END_TIME, SLOT);
//         DoctorSingleShift dsPersisted = em.find(DoctorSingleShift.class, doctorSingleShift.getId());

//         Assert.assertNotNull(dsPersisted);
//         Assert.assertEquals(DOC_ID, dsPersisted.getDoctor().getId());
//         Assert.assertEquals(WEEKDAY, dsPersisted.getWeekday());
//         Assert.assertEquals(ADDRESS, dsPersisted.getAddress());
//         Assert.assertEquals(START_TIME, dsPersisted.getStartTime());
//         Assert.assertEquals(END_TIME, dsPersisted.getEndTime());
//         Assert.assertEquals(SLOT, dsPersisted.getDuration());
//         Assert.assertTrue(dsPersisted.getIsActive());
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
//     public void testGetShiftById(){
//         final Long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
//         SHIFT.setId(SHIFT_ID);
//         SHIFT.getDoctor().setId(TestData.Users.doctorId);
//         SHIFT.getDoctor().getPicture().setId(TestData.Images.validImageId);

//         Optional<DoctorSingleShift> foundShift = doctorSingleShiftDao.getShiftById(SHIFT_ID);

//         Assert.assertNotNull(foundShift);
//         Assert.assertTrue(foundShift.isPresent());
//         Assert.assertEquals(SHIFT, foundShift.get());
//         Assert.assertEquals(SHIFT.getDoctor().getId(), foundShift.get().getDoctor().getId());
//         Assert.assertEquals(SHIFT.getWeekday(), foundShift.get().getWeekday());
//         Assert.assertEquals(SHIFT.getAddress(), foundShift.get().getAddress());
//         Assert.assertEquals(SHIFT.getStartTime(), foundShift.get().getStartTime());
//         Assert.assertEquals(SHIFT.getEndTime(), foundShift.get().getEndTime());
//         Assert.assertEquals(SHIFT.getDuration(), foundShift.get().getDuration());
//         Assert.assertEquals(SHIFT.getIsActive(), foundShift.get().getIsActive());
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
//     public void testGetShiftByIdNonexistentShift(){
//         final Long SHIFT_ID = 0L;

//         Optional<DoctorSingleShift> foundShift = doctorSingleShiftDao.getShiftById(SHIFT_ID);

//         Assert.assertNotNull(foundShift);
//         Assert.assertFalse(foundShift.isPresent());
//     }
    
//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
//     public void updateShiftsNoNewShiftsNull(){
//         final Long DOC_ID = TestData.Users.doctorId;
//         final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;

//         doctorSingleShiftDao.updateShifts(DOC_ID, null);
//         Doctor DOC = em.find(Doctor.class, DOC_ID);
    
//         Assert.assertNotNull(DOC);
//         Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
//         Assert.assertTrue(DOC.getActiveSingleShifts().get(0).getIsActive());
//         Assert.assertEquals(DOC_ID, DOC.getActiveSingleShifts().get(0).getDoctor().getId());
//         Assert.assertEquals(OLD_SHIFT.getWeekday(), DOC.getActiveSingleShifts().get(0).getWeekday());
//         Assert.assertEquals(OLD_SHIFT.getAddress(), DOC.getActiveSingleShifts().get(0).getAddress());
//         Assert.assertEquals(OLD_SHIFT.getStartTime(), DOC.getActiveSingleShifts().get(0).getStartTime());
//         Assert.assertEquals(OLD_SHIFT.getEndTime(), DOC.getActiveSingleShifts().get(0).getEndTime());
//         Assert.assertEquals(OLD_SHIFT.getDuration(), DOC.getActiveSingleShifts().get(0).getDuration());
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
//     public void updateShiftsNoNewShiftsEmpty(){
//         final Long DOC_ID = TestData.Users.doctorId;
//         final DoctorSingleShift OLD_SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;

//         doctorSingleShiftDao.updateShifts(DOC_ID, Collections.emptyList());
//         Doctor DOC = em.find(Doctor.class, DOC_ID);
    
//         Assert.assertNotNull(DOC);
//         Assert.assertEquals(1, DOC.getActiveSingleShifts().size());
//         Assert.assertTrue(DOC.getActiveSingleShifts().get(0).getIsActive());
//         Assert.assertEquals(DOC_ID, DOC.getActiveSingleShifts().get(0).getDoctor().getId());
//         Assert.assertEquals(OLD_SHIFT.getWeekday(), DOC.getActiveSingleShifts().get(0).getWeekday());
//         Assert.assertEquals(OLD_SHIFT.getAddress(), DOC.getActiveSingleShifts().get(0).getAddress());
//         Assert.assertEquals(OLD_SHIFT.getStartTime(), DOC.getActiveSingleShifts().get(0).getStartTime());
//         Assert.assertEquals(OLD_SHIFT.getEndTime(), DOC.getActiveSingleShifts().get(0).getEndTime());
//         Assert.assertEquals(OLD_SHIFT.getDuration(), DOC.getActiveSingleShifts().get(0).getDuration());
//     }

//     // @Test
//     // @Sql({"classpath:images.sql", "classpath:users.sql"})
//     // public void testGetAvailableTurnsByDoctorByDateNullDoc(){

//     //     List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(null, LocalDate.now());

//     //     Assert.assertTrue(avTurns.isEmpty());
//     // }

//     // @Test
//     // @Sql({"classpath:images.sql", "classpath:users.sql"})
//     // public void testGetAvailableTurnsByDoctorByDateNullDate(){
//     //     final Long DOC_ID = TestData.Users.doctorId;
//     //     final Doctor DOC = em.find(Doctor.class, DOC_ID);

//     //     List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(DOC, null);

//     //     Assert.assertTrue(avTurns.isEmpty());
//     // }

//     // @Test
//     // @Sql({"classpath:images.sql", "classpath:users.sql"})
//     // public void testGetAvailableTurnsByDoctorByDateBeforeDate(){
//     //     final Long DOC_ID = TestData.Users.doctorId;
//     //     final Doctor DOC = em.find(Doctor.class, DOC_ID);

//     //     List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(DOC, LocalDate.now().minusDays(1));

//     //     Assert.assertTrue(avTurns.isEmpty());
//     // }

//     // @Test
//     // @Sql({"classpath:images.sql", "classpath:users.sql"})
//     // public void testGetAvailableTurnsByDoctorByDateNoDocShifts(){
//     //     final Long DOC_ID = TestData.Users.doctorId;
//     //     final Doctor DOC = em.find(Doctor.class, DOC_ID);

//     //     List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(DOC, LocalDate.now().plusDays(1));

//     //     Assert.assertTrue(avTurns.isEmpty());
//     // }

//     // @Test
//     // @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
//     // public void testGetAvailableTurnsByDoctorByDateAllTaken(){
//     //     final Long DOC_ID = TestData.Users.doctorId;
//     //     final Doctor DOC = em.find(Doctor.class, DOC_ID);
//     //     final LocalDate DATE = TestData.NewAppointments.appointment.getDate();
//     //     final Long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//     //     final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//     //     final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//     //     final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//     //     List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(DOC, DATE);
//     //     AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, DATE, TIME1, TIME2));
//     //     AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, DATE, TIME2, TIME3));


//     //     Assert.assertTrue(avTurns.isEmpty());
//     //     Assert.assertNotNull(appFound1);
//     //     Assert.assertNotNull(appFound2);
//     // }

//     // @Test
//     // @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
//     // public void testGetAvailableTurnsByDoctorByDateNoneTaken(){
//     //     final Long DOC_ID = TestData.Users.doctorId;
//     //     final Doctor DOC = em.find(Doctor.class, DOC_ID);
//     //     final LocalDate DATE = TestData.NewAppointments.appointment.getDate();
//     //     final Long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//     //     final String ADDRESS = TestData.DoctorSingleShifts.doctorSingleShift.getAddress();
//     //     final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//     //     final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//     //     final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//     //     List<AvailableTurn> avTurns = doctorSingleShiftDao.getAvailableTurnsByDoctorByDate(DOC, DATE);
//     //     AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, DATE, TIME1, TIME2));
//     //     AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, DATE, TIME2, TIME3));


//     //     Assert.assertFalse(avTurns.isEmpty());
//     //     Assert.assertEquals(2, avTurns.size());
//     //     Assert.assertEquals(SHIFT_ID, avTurns.get(0).getShiftId());
//     //     Assert.assertEquals(DATE, avTurns.get(0).getDate());
//     //     Assert.assertEquals(TIME1, avTurns.get(0).getStartTime());
//     //     Assert.assertEquals(TIME2, avTurns.get(0).getEndTime());
//     //     Assert.assertEquals(ADDRESS, avTurns.get(0).getAddress());
//     //     Assert.assertEquals(SHIFT_ID, avTurns.get(1).getShiftId());
//     //     Assert.assertEquals(DATE, avTurns.get(1).getDate());
//     //     Assert.assertEquals(TIME2, avTurns.get(1).getStartTime());
//     //     Assert.assertEquals(TIME3, avTurns.get(1).getEndTime());
//     //     Assert.assertEquals(ADDRESS, avTurns.get(1).getAddress());
//     //     Assert.assertNull(appFound1);
//     //     Assert.assertNull(appFound2);
//     // }

// }