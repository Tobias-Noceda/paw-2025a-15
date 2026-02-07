// package ar.edu.itba.paw.persistence;

// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.List;
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

// import ar.edu.itba.paw.models.entities.AppointmentNew;
// import ar.edu.itba.paw.models.entities.AppointmentNewId;
// import ar.edu.itba.paw.models.entities.Doctor;
// import ar.edu.itba.paw.models.entities.DoctorSingleShift;
// import ar.edu.itba.paw.models.entities.Patient;
// import ar.edu.itba.paw.models.entities.User;
// import ar.edu.itba.paw.persistence.config.TestConfig;

// @Sql("classpath:images.sql")
// @Sql("classpath:users.sql")
// @Sql("classpath:doctorSingleShifts.sql")
// @Transactional
// @Rollback
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = TestConfig.class)
// public class AppointmentsJpaDaoTest {

//     @Autowired
//     private AppointmentJpaDao appointmentDao;

//     @PersistenceContext
//     private EntityManager em;

//     @Before
//     public void setup() {
//         TestData.DoctorSingleShifts.doctorSingleShift.setId(TestData.DoctorSingleShifts.doctorSingleShiftId); // Ensure ID is set before creating APP
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
//     public void testAddAppointment(){
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final long PATIENT_ID = TestData.Users.patientId;
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalTime START_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime END_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final String DETAIL = TestData.NewAppointments.appointment.getDetail();
//         final AppointmentNew APP = TestData.NewAppointments.appointment;
//         APP.getId().setShiftId(SHIFT_ID);
//         APP.getShift().setId(SHIFT_ID);
//         APP.getShift().getDoctor().setId(TestData.Users.doctorId);
//         APP.getPatient().setId(PATIENT_ID);
//         APP.getPatient().getPicture().setId(TestData.Images.validImageId);

//         AppointmentNew appointment = appointmentDao.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, DETAIL);
//         AppointmentNew appPersisted = em.find(AppointmentNew.class, appointment.getId());

//         Assert.assertNotNull(appPersisted);
//         Assert.assertEquals(APP, appPersisted);
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
//     public void testAddAppointmentNonexistentPatient(){
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final long PATIENT_ID = 0L;
//         final LocalTime START_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime END_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final String DETAIL = "Appointment detail 1";
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();

//         AppointmentNew appointment = appointmentDao.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, DETAIL);
//         AppointmentNew appPersisted = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, START_TIME, END_TIME));

//         Assert.assertNull(appointment);
//         Assert.assertNull(appPersisted);
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql"})
//     public void testAddAppointmentNonexistentShift(){
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final LocalTime START_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime END_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final String DETAIL = "Appointment detail 1";
//         final long PATIENT_ID = TestData.Users.patientId;
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();

//         AppointmentNew appointment = appointmentDao.addAppointment(SHIFT_ID, PATIENT_ID, APP_DATE, START_TIME, END_TIME, DETAIL);
//         AppointmentNew appPersisted = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, START_TIME, END_TIME));

//         Assert.assertNull(appointment);
//         Assert.assertNull(appPersisted);
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql"})
//     public void testGetAppointmentsByShiftIdAndDate(){
//         final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         SHIFT.setId(SHIFT_ID);
//         final User PATIENT = TestData.Users.patient;
//         final long PATIENT_ID = TestData.Users.patientId;
//         PATIENT.setId(PATIENT_ID);
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalTime START_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime END_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final AppointmentNew APP = TestData.NewAppointments.appointment;
//         APP.getId().setShiftId(SHIFT_ID);
//         APP.getShift().setId(SHIFT_ID);
//         APP.getShift().getDoctor().setId(TestData.Users.doctorId);
//         APP.getShift().getDoctor().getPicture().setId(TestData.Images.validImageId);
//         APP.setPatient(PATIENT);

//         Optional<AppointmentNew> appointment = appointmentDao.getAppointmentByShiftDateAndTime(SHIFT, APP_DATE, START_TIME, END_TIME);

//         Assert.assertNotNull(appointment);
//         Assert.assertTrue(appointment.isPresent());
//         Assert.assertEquals(APP, appointment.get());
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql"})
//     public void testGetAppointmentsByShiftIdAndDateNonexistentApp(){
//         final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         SHIFT.setId(SHIFT_ID);
//         final User PATIENT = TestData.Users.patient;
//         final long PATIENT_ID = TestData.Users.patientId;
//         PATIENT.setId(PATIENT_ID);
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalTime START_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime END_TIME = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final AppointmentNew APP = TestData.NewAppointments.appointment;
//         APP.getId().setShiftId(SHIFT_ID);
//         APP.getShift().setId(SHIFT_ID);
//         APP.getShift().getDoctor().setId(TestData.Users.doctorId);
//         APP.getShift().getDoctor().getPicture().setId(TestData.Images.validImageId);
//         APP.setPatient(PATIENT);

//         Optional<AppointmentNew> appointment = appointmentDao.getAppointmentByShiftDateAndTime(SHIFT, APP_DATE, START_TIME, END_TIME);

//         Assert.assertNotNull(appointment);
//         Assert.assertFalse(appointment.isPresent());
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql", "classpath:oldNewAppointments.sql"})
//     public void testGetAppointmentsForDate(){
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final long PATIENT_ID = TestData.Users.patientId;
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final AppointmentNew APP = TestData.NewAppointments.appointment;
//         APP.getId().setShiftId(SHIFT_ID);
//         APP.getShift().setId(SHIFT_ID);
//         APP.getShift().getDoctor().setId(TestData.Users.doctorId);
//         APP.getShift().getDoctor().getPicture().setId(TestData.Images.validImageId);
//         APP.getPatient().setId(PATIENT_ID);
//         APP.getPatient().getPicture().setId(TestData.Images.validImageId);
//         final AppointmentNew APP2 = TestData.NewAppointments.appointment2;
//         APP2.getId().setShiftId(SHIFT_ID);
//         APP2.getShift().setId(SHIFT_ID);
//         APP2.getShift().getDoctor().setId(TestData.Users.doctorId);
//         APP2.getShift().getDoctor().getPicture().setId(TestData.Images.validImageId);
//         APP2.getPatient().setId(PATIENT_ID);
//         APP2.getPatient().getPicture().setId(TestData.Images.validImageId);
//         final LocalDate APP_DATE_OLD = TestData.NewAppointments.oldAppointment.getDate();
//         final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//         List<AppointmentNew> results = appointmentDao.getAppointmentsForDate(APP_DATE);
//         AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME1, TIME2));
//         AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME2, TIME3));
//         AppointmentNew appFound3 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME1, TIME2));
//         AppointmentNew appFound4 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME2, TIME3));

//         Assert.assertNotNull(results);
//         Assert.assertNotNull(appFound1);
//         Assert.assertNotNull(appFound2);
//         Assert.assertNotNull(appFound3);
//         Assert.assertNotNull(appFound4);
//         Assert.assertFalse(results.isEmpty());
//         Assert.assertEquals(2, results.size());
//         Assert.assertTrue(results.contains(APP));
//         Assert.assertTrue(results.contains(APP2));
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql", "classpath:oldNewAppointments.sql"})
//     public void testRemoveAppointment(){
//         final DoctorSingleShift SHIFT = TestData.DoctorSingleShifts.doctorSingleShift;
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalDate APP_DATE_OLD = TestData.NewAppointments.oldAppointment.getDate();
//         final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//         boolean result = appointmentDao.cancelAppointment(SHIFT, APP_DATE, TIME1, TIME2);
//         AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME1, TIME2));
//         AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME2, TIME3));
//         AppointmentNew appFound3 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME1, TIME2));
//         AppointmentNew appFound4 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME2, TIME3));

//         Assert.assertTrue(result);
//         Assert.assertNull(appFound1);
//         Assert.assertNotNull(appFound2);
//         Assert.assertNotNull(appFound3);
//         Assert.assertNotNull(appFound4);
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql", "classpath:oldNewAppointments.sql"})
//     public void testRemoveAppointmentNonexistentAppointment(){
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final DoctorSingleShift fakeShift = TestData.DoctorSingleShifts.doctorSingleShift;
//         fakeShift.setId(99L);
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalDate APP_DATE_OLD = TestData.NewAppointments.oldAppointment.getDate();
//         final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//         boolean result = appointmentDao.cancelAppointment(fakeShift, LocalDate.parse("1999-01-01"), LocalTime.parse("00:00:00"), LocalTime.parse("01:00:00"));
//         AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME1, TIME2));
//         AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME2, TIME3));
//         AppointmentNew appFound3 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME1, TIME2));
//         AppointmentNew appFound4 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME2, TIME3));

//         Assert.assertFalse(result);
//         Assert.assertNotNull(appFound1);
//         Assert.assertNotNull(appFound2);
//         Assert.assertNotNull(appFound3);
//         Assert.assertNotNull(appFound4);
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:appointmentsByDoc.sql"})
//     public void testClearRemovedAppointmentBeforeDateApplied(){
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final DoctorSingleShift Shift = TestData.DoctorSingleShifts.doctorSingleShift;
//         Shift.setId(SHIFT_ID);
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalDate APP_DATE_OLD = TestData.NewAppointments.oldAppointment.getDate();
//         final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//         appointmentDao.clearRemovedAppointmentBeforeDate(LocalDate.of(2026, 1, 1));
//         AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME1, TIME2));
//         AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME2, TIME3));
//         AppointmentNew appFound3 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME1, TIME2));
//         AppointmentNew appFound4 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME2, TIME3));

//         Assert.assertNull(appFound1);
//         Assert.assertNull(appFound2);
//         Assert.assertNull(appFound3);
//         Assert.assertNull(appFound4);
//     }
    
//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:appointmentsByDoc.sql"})
//     public void testClearRemovedAppointmentBeforeDateNotApplied(){
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final DoctorSingleShift Shift = TestData.DoctorSingleShifts.doctorSingleShift;
//         Shift.setId(SHIFT_ID);
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalDate APP_DATE_OLD = TestData.NewAppointments.oldAppointment.getDate();
//         final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//         appointmentDao.clearRemovedAppointmentBeforeDate(LocalDate.of(2025, 1, 1));
//         AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME1, TIME2));
//         AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME2, TIME3));
//         AppointmentNew appFound3 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME1, TIME2));
//         AppointmentNew appFound4 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME2, TIME3));

//         Assert.assertNotNull(appFound1);
//         Assert.assertNotNull(appFound2);
//         Assert.assertNotNull(appFound3);
//         Assert.assertNotNull(appFound4);
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql", "classpath:oldNewAppointments.sql"})
//     public void testGetFutureAppointmentDataByPatient(){
//         final Patient PATIENT = TestData.Users.patient;
//         PATIENT.setId(TestData.Users.patientId);
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final DoctorSingleShift Shift = TestData.DoctorSingleShifts.doctorSingleShift;
//         Shift.setId(SHIFT_ID);
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalDate APP_DATE_OLD = TestData.NewAppointments.oldAppointment.getDate();
//         final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//         List<AppointmentNew> apps = appointmentDao.getFutureAppointmentDataByPatient(PATIENT);
//         AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME1, TIME2));
//         AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME2, TIME3));
//         AppointmentNew appFound3 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME1, TIME2));
//         AppointmentNew appFound4 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME2, TIME3));

//         Assert.assertNotNull(apps);
//         Assert.assertFalse(apps.isEmpty());
//         Assert.assertEquals(2, apps.size());
//         Assert.assertTrue(apps.contains(appFound1));
//         Assert.assertTrue(apps.contains(appFound2));
//         Assert.assertNotNull(appFound1);
//         Assert.assertNotNull(appFound2);
//         Assert.assertNotNull(appFound3);
//         Assert.assertNotNull(appFound4);
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql", "classpath:oldNewAppointments.sql"})
//     public void testGetOldAppointmentDataByPatient(){
//         final Patient PATIENT = TestData.Users.patient;
//         PATIENT.setId(TestData.Users.patientId);
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final DoctorSingleShift Shift = TestData.DoctorSingleShifts.doctorSingleShift;
//         Shift.setId(SHIFT_ID);
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalDate APP_DATE_OLD = TestData.NewAppointments.oldAppointment.getDate();
//         final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//         List<AppointmentNew> apps = appointmentDao.getOldAppointmentDataByPatient(PATIENT);
//         AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME1, TIME2));
//         AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME2, TIME3));
//         AppointmentNew appFound3 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME1, TIME2));
//         AppointmentNew appFound4 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME2, TIME3));

//         Assert.assertNotNull(apps);
//         Assert.assertFalse(apps.isEmpty());
//         Assert.assertEquals(2, apps.size());
//         Assert.assertTrue(apps.contains(appFound3));
//         Assert.assertTrue(apps.contains(appFound4));
//         Assert.assertNotNull(appFound1);
//         Assert.assertNotNull(appFound2);
//         Assert.assertNotNull(appFound3);
//         Assert.assertNotNull(appFound4);
//     }

//     @Test
//     @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:doctorSingleShifts.sql", "classpath:newAppointments.sql", "classpath:oldNewAppointments.sql"})
//     public void testGetFutureAppointmentDataByDoctor(){
//         final Doctor DOC = TestData.Users.doctor;
//         DOC.setId(TestData.Users.doctorId);
//         final long SHIFT_ID = TestData.DoctorSingleShifts.doctorSingleShiftId;
//         final DoctorSingleShift Shift = TestData.DoctorSingleShifts.doctorSingleShift;
//         Shift.setId(SHIFT_ID);
//         final LocalDate APP_DATE = TestData.NewAppointments.appointment.getDate();
//         final LocalDate APP_DATE_OLD = TestData.NewAppointments.oldAppointment.getDate();
//         final LocalTime TIME1 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime();
//         final LocalTime TIME2 = TestData.DoctorSingleShifts.doctorSingleShift.getStartTime().plusMinutes(TestData.DoctorSingleShifts.doctorSingleShift.getDuration());
//         final LocalTime TIME3 = TestData.DoctorSingleShifts.doctorSingleShift.getEndTime();

//         List<AppointmentNew> apps = appointmentDao.getFutureAppointmentDataByDoctor(DOC);
//         AppointmentNew appFound1 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME1, TIME2));
//         AppointmentNew appFound2 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE, TIME2, TIME3));
//         AppointmentNew appFound3 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME1, TIME2));
//         AppointmentNew appFound4 = em.find(AppointmentNew.class, new AppointmentNewId(SHIFT_ID, APP_DATE_OLD, TIME2, TIME3));

//         Assert.assertNotNull(apps);
//         Assert.assertFalse(apps.isEmpty());
//         Assert.assertEquals(2, apps.size());
//         Assert.assertTrue(apps.contains(appFound1));
//         Assert.assertTrue(apps.contains(appFound2));
//         Assert.assertNotNull(appFound1);
//         Assert.assertNotNull(appFound2);
//         Assert.assertNotNull(appFound3);
//         Assert.assertNotNull(appFound4);
//     }
    
// }
