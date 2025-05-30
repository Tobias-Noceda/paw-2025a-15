/*package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.entities.Appointment;
import ar.edu.itba.paw.models.entities.AuthDoctor;
import ar.edu.itba.paw.models.entities.DoctorCoverage;
import ar.edu.itba.paw.models.entities.DoctorDetail;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.PatientDetail;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

//Readable output of all the data inputed in test/resources
public class TestData {

    public class Images{
        public static final File validImage = new File("D".getBytes(), FileTypeEnum.JPEG);
        public static final long validImageId = 1L;
        public static final File validImage2 = new File("O".getBytes(), FileTypeEnum.JPEG);
        public static final long validImage2Id = 2L;
        public static final File newImage = new File("N".getBytes(), FileTypeEnum.JPEG);
    }

    public class Insurances{
        public static final Insurance validInsurance = new Insurance("OSDE", Images.validImage);
        public static final long validInsuranceId = 1L;
        public static final Insurance validInsurance2 = new Insurance("Galeno", Images.validImage);
        public static final long validInsurance2Id = 2L;
        public static final Insurance newInsurance = new Insurance("Swiss Medical", Images.validImage);
    }

    public class Studies{
        public static final Study validStudyWithDate = new Study(StudyTypeEnum.OTHER, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.parse("2025-04-10 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final long validStudyWithDateId = 1L;
        public static final Study validStudyWithoutDate = new Study(StudyTypeEnum.OTHER, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.parse("2025-04-09 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final long validStudyWithoutDateId = 2L;
        public static final Study extraStudy = new Study(StudyTypeEnum.PRESCRIPTION, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.parse("2025-04-10 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final long extraStudyId = 3L;
        public static final Study extraStudy2 = new Study(StudyTypeEnum.PRESCRIPTION, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.parse("2025-04-09 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final long extraStudy2Id = 4L;
        public static final Study newStudyWithDate = new Study(StudyTypeEnum.OTHER, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.now(), LocalDate.parse("2025-04-09"));
        public static final Study newStudyWithoutDate = new Study(StudyTypeEnum.OTHER, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.now(), LocalDate.now());
        public static final long newStudyId = 3L;
    }

    public class Users{
        public static final User patient = new User("donna@example.com", "supersecret123", "donna", "1144445555", UserRoleEnum.PATIENT, Images.validImage, LocalDate.parse("2025-04-09") , LocaleEnum.EN_US);
        public static final long patientId = 1L;
        public static final User newPatient = new User("sophie@example.com", "supersecret123", "sophie", "1144445555", UserRoleEnum.PATIENT, Images.validImage, LocalDate.now() , LocaleEnum.EN_US);
        public static final long newPatientId = 3L;
        

        public static final User doctor = new User("membrillo@example.com", "supersecret123", "membrillo", "1144445555", UserRoleEnum.DOCTOR, Images.validImage, LocalDate.parse("2025-04-09") , LocaleEnum.EN_US);
        public static final long doctorId = 2L;
        public static final User newDoctor = new User("batata@example.com", "supersecret123", "batata", "1144445555", UserRoleEnum.DOCTOR, Images.validImage, LocalDate.now() , LocaleEnum.EN_US);
        public static final long newDoctorId = 3L;
    }

    public class DoctorDetails{
        public static final DoctorDetail doctorDetail = new DoctorDetail(Users.doctor, "med-licence", SpecialtyEnum.CARDIOLOGY);
    }

    public class DoctorCoverages{
        public static final DoctorCoverage doctorCoverage = new DoctorCoverage(Users.doctor, Insurances.validInsurance);
        public static final DoctorCoverage doctorCoverage2 = new DoctorCoverage(Users.doctor, Insurances.validInsurance2);
    }

    public class DoctorShifts{
        public static final DoctorShift doctorShift = new DoctorShift(Users.doctor, WeekdayEnum.THURSDAY, "Lavarden", LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"));
        public static final long doctorShiftId = 1L;
        public static final DoctorShift doctorShift2 = new DoctorShift(Users.doctor, WeekdayEnum.THURSDAY, "Lavarden", LocalTime.parse("10:15:00"), LocalTime.parse("10:30:00"));
        public static final long doctorShift2Id = 2L;
    }

    public class Appointments{
        public static final Appointment appointment = new Appointment(DoctorShifts.doctorShift, Users.patient, LocalDate.parse("2025-10-09"));
        public static final Appointment appointment2 = new Appointment(DoctorShifts.doctorShift2, Users.patient, LocalDate.parse("2025-10-09"));
        public static final Appointment oldAppointment = new Appointment(DoctorShifts.doctorShift, Users.patient, LocalDate.parse("2025-04-09"));
        public static final Appointment oldAppointment2 = new Appointment(DoctorShifts.doctorShift, Users.patient, LocalDate.parse("2025-04-09"));
        public static final Appointment removedAppointment = new Appointment(DoctorShifts.doctorShift2, Users.patient, LocalDate.parse("2025-04-19"));
        public static final Appointment removedAppointment2 = new Appointment(DoctorShifts.doctorShift, Users.doctor, LocalDate.parse("2025-04-19"));
    }

    public class PatientDetails{
        public static final PatientDetail patientDetail = new PatientDetail(Users.patient, null, null, null, null, null, null, null, null, null, null, null, "carpenter");
        public static final PatientDetail newPatientDetail = new PatientDetail(Users.patient, LocalDate.parse("2000-01-01"), BloodTypeEnum.AB_POSITIVE, 1.75, 89.00, false, true, null, null, null, null, null, null);
        public static final PatientDetail newPatientDetailNotNullValues = new PatientDetail(Users.patient, LocalDate.parse("2000-01-01"), BloodTypeEnum.AB_POSITIVE, 1.75, 89.00, false, true, "a", "a", "a", "a", "a", "a");
        public static final PatientDetail newPatientDetailNullValues = new PatientDetail(Users.patient, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public class AuthDoctors{
        public static final AuthDoctor authDoctor = new AuthDoctor(Users.doctor, Users.patient, AccessLevelEnum.VIEW_BASIC);
        public static final AuthDoctor authDoctorSocialLevel = new AuthDoctor(Users.doctor, Users.patient, AccessLevelEnum.VIEW_SOCIAL);
    }
}
*/