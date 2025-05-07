package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AuthDoctor;
import ar.edu.itba.paw.models.DoctorCoverage;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.PatientDetail;

//Readable output of all the data inputed in test/resources
public class TestData {

    public class Images{
        public static final File validImage = new File(1L, "D".getBytes(), FileTypeEnum.JPEG);
        public static final File validImage2 = new File(2L, "O".getBytes(), FileTypeEnum.JPEG);
        public static final File newImage = new File(0, "N".getBytes(), FileTypeEnum.JPEG);
    }

    public class Insurances{
        public static final Insurance validInsurance = new Insurance(1, "OSDE", 1);
        public static final Insurance validInsurance2 = new Insurance(2, "Galeno", 1);
        public static final Insurance newInsurance = new Insurance(3, "Swiss Medical", 1);
    }

    public class Studies{
        public static final Study validStudyWithDate = new Study(1, StudyTypeEnum.OTHER, "simple image", 1, 1, 1, LocalDateTime.parse("2025-04-10 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Study validStudyWithoutDate = new Study(2, StudyTypeEnum.OTHER, "simple image", 1, 1, 1, LocalDateTime.parse("2025-04-09 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Study newStudyWithDate = new Study(3, StudyTypeEnum.OTHER, "simple image", 1, 1, 1, LocalDateTime.now(), LocalDate.parse("2025-04-09"));
        public static final Study newStudyWithoutDate = new Study(3, StudyTypeEnum.OTHER, "simple image", 1, 1, 1, LocalDateTime.now(), LocalDate.now());
    }

    public class Users{
        public static final User patient = new User(1, "donna@example.com", "supersecret123", "donna", "1144445555", UserRoleEnum.PATIENT, 1, LocalDate.parse("2025-04-09") , LocaleEnum.EN_US);
        public static final User newPatient = new User(3, "sophie@example.com", "supersecret123", "sophie", "1144445555", UserRoleEnum.PATIENT, 1, LocalDate.now() , LocaleEnum.EN_US);


        public static final User doctor = new User(2, "membrillo@example.com", "supersecret123", "membrillo", "1144445555", UserRoleEnum.DOCTOR, 1, LocalDate.parse("2025-04-09") , LocaleEnum.EN_US);
        public static final User newDoctor = new User(3, "batata@example.com", "supersecret123", "batata", "1144445555", UserRoleEnum.DOCTOR, 1, LocalDate.now() , LocaleEnum.EN_US);
    }

    public class DoctorDetails{
        public static final DoctorDetail doctorDetail = new DoctorDetail(2, "med-licence", SpecialtyEnum.CARDIOLOGY);
    }

    public class DoctorCoverages{
        public static final DoctorCoverage doctorCoverage = new DoctorCoverage(2, 1);
        public static final DoctorCoverage doctorCoverage2 = new DoctorCoverage(2, 2);
    }

    public class DoctorShifts{
        public static final DoctorShift doctorShift = new DoctorShift(1, 2, WeekdayEnum.THURSDAY, "Lavarden", LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"));
        public static final DoctorShift doctorShift2 = new DoctorShift(2, 2, WeekdayEnum.THURSDAY, "Lavarden", LocalTime.parse("10:15:00"), LocalTime.parse("10:30:00"));
    }

    public class Appointments{
        public static final Appointment appointment = new Appointment(1, 1, LocalDate.parse("2025-10-09"));
        public static final Appointment appointment2 = new Appointment(2, 1, LocalDate.parse("2025-10-09"));
        public static final Appointment oldAppointment = new Appointment(1, 1, LocalDate.parse("2025-04-09"));
        public static final Appointment oldAppointment2 = new Appointment(1, 1, LocalDate.parse("2025-04-09"));
        public static final Appointment removedAppointment = new Appointment(2, 1, LocalDate.parse("2025-04-19"));
        public static final Appointment removedAppointment2 = new Appointment(1, 2, LocalDate.parse("2025-04-19"));
    }

    public class PatientDetails{
        public static final PatientDetail patientDetail = new PatientDetail(1, null, null, null, null, null, null, null, null, null, null, null, "carpenter");
        public static final PatientDetail newPatientDetail = new PatientDetail(1, 30, BloodTypeEnum.AB_POSITIVE, 1.75, 89.00, false, true, null, null, null, null, null, null);
        public static final PatientDetail newPatientDetailNotNullValues = new PatientDetail(1, 30, BloodTypeEnum.AB_POSITIVE, 1.75, 89.00, false, true, "a", "a", "a", "a", "a", "a");
        public static final PatientDetail newPatientDetailNullValues = new PatientDetail(1, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public class AuthDoctors{
        public static final AuthDoctor authDoctor = new AuthDoctor(2, 1, AccessLevelEnum.VIEW_BASIC);
        public static final AuthDoctor authDoctorSocialLevel = new AuthDoctor(2, 1, AccessLevelEnum.VIEW_SOCIAL);
    }
}
