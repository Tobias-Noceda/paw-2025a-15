package ar.edu.itba.paw.persistence;

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

import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Sql("classpath:users.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJpaDaoTest {
    
    @Autowired
    private UserJpaDao userDao;

    @PersistenceContext
    private EntityManager em;
/*TODO:rethink
    @Test
    public void testCreate(){
        final String USEREMAIL = TestData.Users.newPatient.getEmail();
        final String PASSWORD = TestData.Users.newPatient.getPassword();
        final String USERNAME = TestData.Users.newPatient.getName();
        final String USER_TELEPHONE = TestData.Users.newPatient.getTelephone();
        final UserRoleEnum USER_ROLE = TestData.Users.newPatient.getRole();
        final File PICTURE = TestData.Users.newPatient.getPicture();
        PICTURE.setId(TestData.Images.validImageId);
        final LocaleEnum USER_LOCALE = TestData.Users.newPatient.getLocale();
        
        User user = userDao.create(USEREMAIL, PASSWORD, USERNAME, USER_TELEPHONE, USER_ROLE, PICTURE, USER_LOCALE);
        User userPersisted = em.find(User.class, user.getId());

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USEREMAIL, userPersisted.getEmail());
        Assert.assertEquals(PASSWORD, userPersisted.getPassword());
        Assert.assertEquals(USERNAME, userPersisted.getName());
        Assert.assertEquals(USER_TELEPHONE, userPersisted.getTelephone());
        Assert.assertEquals(USER_ROLE, userPersisted.getRole());
        Assert.assertEquals(PICTURE, userPersisted.getPicture());
        Assert.assertEquals(USER_LOCALE, userPersisted.getLocale());
    }*/
/*TODO:rethink
    @Test
    public void testCreateExistentEmail(){
        final String USEREMAIL = TestData.Users.patient.getEmail();
        final String PASSWORD = TestData.Users.newPatient.getPassword();
        final String USERNAME = TestData.Users.newPatient.getName();
        final String USER_TELEPHONE = TestData.Users.newPatient.getTelephone();
        final UserRoleEnum USER_ROLE = TestData.Users.newPatient.getRole();
        final File PICTURE = TestData.Users.newPatient.getPicture();
        PICTURE.setId(TestData.Images.validImageId);
        final LocaleEnum USER_LOCALE = TestData.Users.newPatient.getLocale();

        Assert.assertThrows(PersistenceException.class, () -> {
            userDao.create(USEREMAIL, PASSWORD, USERNAME, USER_TELEPHONE, USER_ROLE, PICTURE, USER_LOCALE);
            em.flush();
        });
    }*/

    @Test
    public void testGetUserById(){
        final User USER = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER.setId(USER_ID);
        USER.getPicture().setId(TestData.Images.validImageId);

        Optional<User> foundUser = userDao.getUserById(USER_ID);

        Assert.assertNotNull(foundUser);
        Assert.assertTrue(foundUser.isPresent());
        Assert.assertEquals(USER, foundUser.get());
    }

    @Test
    public void testGetUserByIdNonexistent(){
        final long USER_ID = TestData.Users.newPatientId;

        Optional<User> foundUser = userDao.getUserById(USER_ID);

        Assert.assertNotNull(foundUser);
        Assert.assertFalse(foundUser.isPresent());
    }

    @Test
    public void testGetUserByEmail(){
        final User USER = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER.setId(USER_ID);
        USER.getPicture().setId(TestData.Images.validImageId);
        final String USEREMAIL = TestData.Users.patient.getEmail();

        Optional<User> foundUser = userDao.getUserByEmail(USEREMAIL);

        Assert.assertNotNull(foundUser);
        Assert.assertTrue(foundUser.isPresent());
        Assert.assertEquals(USER, foundUser.get());
    }

    @Test
    public void testGetUserByEmailNonexistent(){
        final String USEREMAIL = TestData.Users.newPatient.getEmail();

        Optional<User> foundUser = userDao.getUserByEmail(USEREMAIL);

        Assert.assertNotNull(foundUser);
        Assert.assertFalse(foundUser.isPresent());
    }

    @Test
    public void testChangePasswordByID(){
        final long USER_ID = TestData.Users.patientId;
        final Patient USER_OLD = TestData.Users.patient;
        USER_OLD.getPicture().setId(TestData.Images.validImageId);
        final String PASSWORD = TestData.Users.patient.getPassword();
        final String NEW_PASSWORD = PASSWORD + "1";
        final Patient USER_NEW = new Patient(USER_OLD.getEmail(), NEW_PASSWORD, USER_OLD.getName(), USER_OLD.getTelephone(), USER_OLD.getPicture(), USER_OLD.getCreateDate(), USER_OLD.getLocale(), USER_OLD.getBirthdate(), USER_OLD.getHeight(), USER_OLD.getWeight());
        USER_NEW.setId(USER_ID);
        USER_OLD.setId(USER_ID);

        userDao.changePasswordByID(USER_ID, NEW_PASSWORD);
        Patient userPersisted = em.find(Patient.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USER_NEW.getPassword(), userPersisted.getPassword());
        Assert.assertNotEquals(USER_OLD.getPassword(), userPersisted.getPassword());
    }

    @Test
    public void testChangePasswordByIDNonexistentUser(){
        final long USER_ID = 0;
        final String PASSWORD = TestData.Users.patient.getPassword();

        userDao.changePasswordByID(USER_ID, PASSWORD);
        User userPersisted = em.find(User.class, USER_ID);

        Assert.assertNull(userPersisted);
    }

    @Test
    public void testEditUser(){
        final Patient USER_OLD = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER_OLD.setId(USER_ID);
        USER_OLD.getPicture().setId(TestData.Images.validImageId);
        final String NEW_TELEPHONE = "1111111111";
        final String NEW_USERNAME = USER_OLD.getName() + "1";
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);
        final Patient USER_NEW = new Patient(USER_OLD.getEmail(), USER_OLD.getPassword(), NEW_USERNAME, NEW_TELEPHONE, NEW_PICTURE, USER_OLD.getCreateDate(), USER_OLD.getLocale(), USER_OLD.getBirthdate(), USER_OLD.getHeight(), USER_OLD.getWeight());
        USER_NEW.setId(USER_ID);

        userDao.editUser(USER_ID, NEW_USERNAME, NEW_TELEPHONE, NEW_PICTURE);
        Patient userPersisted = em.find(Patient.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USER_NEW.getName(), userPersisted.getName());
        Assert.assertEquals(USER_NEW.getTelephone(), userPersisted.getTelephone());
        Assert.assertEquals(USER_NEW.getPicture(), userPersisted.getPicture());
        Assert.assertNotEquals(USER_OLD.getName(), userPersisted.getName());   
        Assert.assertNotEquals(USER_OLD.getTelephone(), userPersisted.getTelephone()); 
        Assert.assertNotEquals(USER_OLD.getPicture(), userPersisted.getPicture());  
    }

    @Test
    public void testEditUserNonexistentUser(){
        final long USER_ID = 0;
        final String USERNAME = TestData.Users.patient.getName();
        final String NEW_TELEPHONE = "1111111111";
        final String NEW_USERNAME = USERNAME + "1";
        final File NEW_PICTURE = TestData.Images.validImage2;

        userDao.editUser(USER_ID, NEW_USERNAME, NEW_TELEPHONE, NEW_PICTURE);
        User userPersisted = em.find(User.class, USER_ID);

        Assert.assertNull(userPersisted);
   }

   @Test
    public void testEditUserNameOnly(){
        final Patient USER_OLD = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER_OLD.setId(USER_ID);
        final String USER_TELEPHONE = USER_OLD.getTelephone();
        final File PICTURE = TestData.Images.validImage;
        PICTURE.setId(TestData.Images.validImageId);
        USER_OLD.setPicture(PICTURE);
        final String NEW_USERNAME = USER_OLD.getName() + "1";
        final Patient USER_NEW = new Patient(USER_OLD.getEmail(), USER_OLD.getPassword(), NEW_USERNAME, USER_TELEPHONE, PICTURE, USER_OLD.getCreateDate(), USER_OLD.getLocale(), USER_OLD.getBirthdate(), USER_OLD.getHeight(), USER_OLD.getWeight());
        USER_NEW.setId(USER_ID);

        userDao.editUser(USER_ID, NEW_USERNAME, USER_TELEPHONE, PICTURE);
        Patient userPersisted = em.find(Patient.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USER_NEW, userPersisted);
        Assert.assertEquals(USER_NEW.getName(), userPersisted.getName());
        Assert.assertNotEquals(USER_OLD.getName(), userPersisted.getName());    
    }

    @Test
    public void testEditUserTelephoneOnly(){
        final Patient USER_OLD = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER_OLD.setId(USER_ID);
        USER_OLD.getPicture().setId(TestData.Images.validImageId);
        final String NEW_TELEPHONE = "1111111111";
        final String USERNAME = USER_OLD.getName();
        final File PICTURE = TestData.Images.validImage;
        PICTURE.setId(TestData.Images.validImageId);
        final Patient USER_NEW = new Patient(USER_OLD.getEmail(), USER_OLD.getPassword(), USERNAME, NEW_TELEPHONE, PICTURE, USER_OLD.getCreateDate(), USER_OLD.getLocale(), USER_OLD.getBirthdate(), USER_OLD.getHeight(), USER_OLD.getWeight());
        USER_NEW.setId(USER_ID);

        userDao.editUser(USER_ID, USERNAME, NEW_TELEPHONE, PICTURE);
        Patient userPersisted = em.find(Patient.class, USER_ID);
        
        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USER_NEW, userPersisted);
        Assert.assertEquals(USER_NEW.getTelephone(), userPersisted.getTelephone());
        Assert.assertNotEquals(USER_OLD.getTelephone(), userPersisted.getTelephone()); 
    }

    @Test
    public void testEditUserPicOnly(){
        final Patient USER_OLD = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER_OLD.setId(USER_ID);
        USER_OLD.getPicture().setId(TestData.Images.validImageId);
        final String USER_TELEPHONE = USER_OLD.getTelephone();
        final String USERNAME = USER_OLD.getName();
        final File NEW_PICTURE = TestData.Images.validImage2;
        NEW_PICTURE.setId(TestData.Images.validImage2Id);
        final Patient USER_NEW = new Patient(USER_OLD.getEmail(), USER_OLD.getPassword(), USERNAME, USER_TELEPHONE, NEW_PICTURE, USER_OLD.getCreateDate(), USER_OLD.getLocale(), USER_OLD.getBirthdate(), USER_OLD.getHeight(), USER_OLD.getWeight());
        USER_NEW.setId(USER_ID);

        userDao.editUser(USER_ID, USERNAME, USER_TELEPHONE, NEW_PICTURE);
        Patient userPersisted = em.find(Patient.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USER_NEW, userPersisted);
        Assert.assertEquals(USER_NEW.getPicture(), userPersisted.getPicture());
        Assert.assertNotEquals(USER_OLD.getPicture(), userPersisted.getPicture());  
    }

    @Test
    public void testUpdateLocale(){
        final Patient USER_OLD = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER_OLD.setId(USER_ID);
        USER_OLD.getPicture().setId(TestData.Images.validImageId);
        final LocaleEnum NEW_LOCALE = LocaleEnum.ES_AR;
        final Patient USER_NEW = new Patient(USER_OLD.getEmail(), USER_OLD.getPassword(), USER_OLD.getName(), USER_OLD.getTelephone(), USER_OLD.getPicture(), USER_OLD.getCreateDate(), NEW_LOCALE, USER_OLD.getBirthdate(), USER_OLD.getHeight(), USER_OLD.getWeight());
        USER_NEW.setId(USER_ID);

        userDao.updateLocale(USER_ID, NEW_LOCALE);
        Patient userPersisted = em.find(Patient.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USER_NEW.getLocale(), userPersisted.getLocale());
        Assert.assertNotEquals(USER_OLD.getLocale(), userPersisted.getLocale());   
    }

    @Test
    public void testUpdateLocaleNonexistentUser(){
        final long USER_ID = 100L;
        final LocaleEnum NEW_LOCALE = LocaleEnum.ES_AR;

        userDao.updateLocale(USER_ID, NEW_LOCALE);
        User userPersisted = em.find(User.class, USER_ID);

        Assert.assertNull(userPersisted);
    }

    @Test
    public void testUpdateLocaleNullLocale(){
        final User USER = TestData.Users.patient;
        final long USER_ID = TestData.Users.patientId;
        USER.setId(USER_ID);
        final LocaleEnum NEW_LOCALE = null;

        userDao.updateLocale(USER_ID, NEW_LOCALE);
        User userPersisted = em.find(User.class, USER_ID);

        Assert.assertNotNull(userPersisted);
        Assert.assertEquals(USER, userPersisted);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndName(){
        final long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = TestData.Users.patient.getName();
        int results = userDao.searchAuthPatientsCountByDoctorIdAndName(DOC_ID, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndNameNullName(){
        final long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = null;

        int results = userDao.searchAuthPatientsCountByDoctorIdAndName(DOC_ID, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsCountByDoctorIdAndNameBlankSpaceName(){
        final long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = "";

        int results = userDao.searchAuthPatientsCountByDoctorIdAndName(DOC_ID, PATIENT_NAME);

        Assert.assertEquals(1, results);
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndName(){
        final long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = TestData.Users.patient.getName();
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<User> results = userDao.searchAuthPatientsPageByDoctorIdAndName(DOC_ID, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameWrongPage(){
        final long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = TestData.Users.patient.getName();

        List<User> results = userDao.searchAuthPatientsPageByDoctorIdAndName(DOC_ID, PATIENT_NAME, 0, 2);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameWrongPageSize(){
        final long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = TestData.Users.patient.getName();

        List<User> results = userDao.searchAuthPatientsPageByDoctorIdAndName(DOC_ID, PATIENT_NAME, 1, 0);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameNullName(){
        final long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = null;
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<User> results = userDao.searchAuthPatientsPageByDoctorIdAndName(DOC_ID, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
    }

    @Test
    @Sql({"classpath:images.sql", "classpath:users.sql", "classpath:authDoctors.sql", "classpath:authDoctors-SocialLevel.sql"})
    public void testSearchAuthPatientsPageByDoctorIdAndNameBlankName(){
        final long DOC_ID = TestData.Users.doctorId;
        final String PATIENT_NAME = "";
        final User PATIENT = TestData.Users.patient;
        PATIENT.setId(TestData.Users.patientId);

        List<User> results = userDao.searchAuthPatientsPageByDoctorIdAndName(DOC_ID, PATIENT_NAME, 1, 2);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(PATIENT, results.get(0));
    }

}