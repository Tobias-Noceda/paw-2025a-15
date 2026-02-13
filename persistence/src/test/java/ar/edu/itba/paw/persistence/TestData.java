package ar.edu.itba.paw.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.AuthDoctor;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

//Readable output of all the data inputed in test/resources
public class TestData {

    public class Images{
        public static final File validImage = new File("D".getBytes(), FileTypeEnum.JPEG);
        public static final Long validImageId = 1L;
        public static final File validImage2 = new File("O".getBytes(), FileTypeEnum.JPEG);
        public static final Long validImage2Id = 2L;
        public static final File newImage = new File("N".getBytes(), FileTypeEnum.JPEG);
    }

    public class Insurances{
        public static final Insurance validInsurance = new Insurance("OSDE", Images.validImage);
        public static final Long validInsuranceId = 1L;
        public static final Insurance validInsurance2 = new Insurance("Galeno", Images.validImage);
        public static final Long validInsurance2Id = 2L;
        public static final Insurance newInsurance = new Insurance("Swiss Medical", Images.validImage);
        public static final Long newInsuranceId = 3L;
    }

    public class Studies{
        public static final Study validStudyWithDate = new Study(StudyTypeEnum.OTHER, "simple image", List.of(Images.validImage), Users.patient, Users.patient, LocalDateTime.parse("2025-04-10 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Long validStudyWithDateId = 1L;
        public static final Study validStudyWithoutDate = new Study(StudyTypeEnum.OTHER, "simple image", List.of(Images.validImage), Users.patient, Users.patient, LocalDateTime.parse("2025-04-09 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Long validStudyWithoutDateId = 2L;
        public static final Study extraStudy = new Study(StudyTypeEnum.PRESCRIPTION, "simple image", List.of(Images.validImage), Users.patient, Users.patient, LocalDateTime.parse("2025-04-10 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Long extraStudyId = 3L;
        public static final Study extraStudy2 = new Study(StudyTypeEnum.PRESCRIPTION, "simple image", List.of(Images.validImage), Users.patient, Users.patient, LocalDateTime.parse("2025-04-09 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Long extraStudy2Id = 4L;
        public static final Study newStudyWithDate = new Study(StudyTypeEnum.OTHER, "simple image", List.of(Images.validImage), Users.patient, Users.patient, LocalDateTime.now(), LocalDate.parse("2025-04-09"));
        public static final Study newStudyWithoutDate = new Study(StudyTypeEnum.OTHER, "simple image", List.of(Images.validImage), Users.patient, Users.patient, LocalDateTime.now(), LocalDate.now());
        public static final Long newStudyId = 3L;
    }

    public class Users{
        public static final Patient patient = new Patient("donna@example.com", "supersecret123", "donna", "1144445555", Images.validImage, LocalDate.parse("2025-04-09") , LocaleEnum.EN_US, LocalDate.parse("2000-01-01"), BigDecimal.valueOf(1.75), BigDecimal.valueOf(89.00));
        public static final Long patientId = 1L;
        public static final Patient newPatient = new Patient("sophie@example.com", "supersecret123", "sophie", "1144445555", Images.validImage, LocalDate.now() , LocaleEnum.EN_US, LocalDate.parse("2000-01-01"), BigDecimal.valueOf(1.75), BigDecimal.valueOf(89.00));
        public static final Long newPatientId = 3L;
        

        public static final Doctor doctor = new Doctor("membrillo@example.com", "supersecret123", "membrillo", "1144445555", Images.validImage, LocalDate.parse("2025-04-09") , LocaleEnum.EN_US, "med-licence", SpecialtyEnum.CARDIOLOGY, List.of());
        public static final Long doctorId = 2L;
        public static final Doctor newDoctor = new Doctor("batata@example.com", "supersecret123", "batata", "1144445555", Images.validImage, LocalDate.now() , LocaleEnum.EN_US, "med-licence", SpecialtyEnum.CARDIOLOGY, List.of());
        public static final Long newDoctorId = 3L;
    }

    public class PatientDetails{
        public static final LocalDate BIRTHDATE = LocalDate.parse("2000-01-01");
        public static final BloodTypeEnum BLOOD_TYPE_1 = null;
        public static final BloodTypeEnum BLOOD_TYPE_2 = BloodTypeEnum.AB_POSITIVE;
        public static final BigDecimal HEIGHT = BigDecimal.valueOf(1.75);
        public static final BigDecimal WEIGHT = BigDecimal.valueOf(89.00);
        public static final Boolean SMOKES_1 = null;
        public static final Boolean SMOKES_2 = false;
        public static final Boolean DRINKS_1 = null;
        public static final Boolean DRINKS_2 = true;
        public static final String MEDS = null;
        public static final String CONDITIONS = null;
        public static final String ALLERGIES = null;
        public static final String DIET = null;
        public static final String HOBBIES = null;
        public static final String JOB_1 = "carpenter";
        public static final String JOB_2 = null;
    }

    public class DoctorSingleShifts{
        public static final DoctorSingleShift doctorSingleShift = new DoctorSingleShift(Users.doctor, WeekdayEnum.THURSDAY, "Lavarden", LocalTime.parse("10:00:00"), LocalTime.parse("10:30:00"), 15);
        public static final Long doctorSingleShiftId = 1L;
        public static DoctorSingleShift doctorInactiveSingleShift = new DoctorSingleShift(Users.doctor, WeekdayEnum.THURSDAY, "Lavarden", LocalTime.parse("13:00:00"), LocalTime.parse("13:30:00"), 15);
        public static final Long doctorInactiveSingleShiftId = 2L;
        public static final DoctorSingleShift newDoctorSingleShift = new DoctorSingleShift(Users.doctor, WeekdayEnum.MONDAY, "Los Patos", LocalTime.parse("15:00:00"), LocalTime.parse("17:00:00"), 30);
        public static final Long newDoctorSingleShiftId = 3L;
    }

    public class NewAppointments{
        public static final AppointmentNew appointment = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2125-10-11"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"), "Appointment detail 1");
        public static final AppointmentNew appointment2 = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2125-10-11"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"), "Appointment detail 2");
        public static final AppointmentNew oldAppointment = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2025-04-09"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"), "Appointment detail 3");
        public static final AppointmentNew oldAppointment2 = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2025-04-09"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"), "Appointment detail 4");
        public static final AppointmentNew removedAppointment = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2025-04-19"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"), "Appointment detail 5");
        public static final AppointmentNew removedAppointment2 = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.doctor, LocalDate.parse("2025-04-19"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"), "Appointment detail 6");
    }

    public class AuthDoctors{
        public static final AuthDoctor authDoctor = new AuthDoctor(Users.doctor, Users.patient, AccessLevelEnum.VIEW_BASIC);
        public static final AuthDoctor authDoctorSocialLevel = new AuthDoctor(Users.doctor, Users.patient, AccessLevelEnum.VIEW_SOCIAL);
    }

    public class DocVacations{
        public static final DoctorVacation docVacation = new DoctorVacation(Users.doctor, LocalDate.parse("2125-04-10"), LocalDate.parse("2125-04-19"));
        public static final DoctorVacation docVacationPast = new DoctorVacation(Users.doctor, LocalDate.parse("2025-04-10"), LocalDate.parse("2025-04-19"));
    }
}
