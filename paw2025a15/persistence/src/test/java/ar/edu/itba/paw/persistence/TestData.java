package ar.edu.itba.paw.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.AuthDoctor;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorCoverage;
import ar.edu.itba.paw.models.entities.DoctorDetail;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.PatientDetail;
import ar.edu.itba.paw.models.entities.Study;
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
        public static final Study validStudyWithDate = new Study(StudyTypeEnum.OTHER, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.parse("2025-04-10 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Long validStudyWithDateId = 1L;
        public static final Study validStudyWithoutDate = new Study(StudyTypeEnum.OTHER, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.parse("2025-04-09 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Long validStudyWithoutDateId = 2L;
        public static final Study extraStudy = new Study(StudyTypeEnum.PRESCRIPTION, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.parse("2025-04-10 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Long extraStudyId = 3L;
        public static final Study extraStudy2 = new Study(StudyTypeEnum.PRESCRIPTION, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.parse("2025-04-09 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDate.parse("2025-04-09"));
        public static final Long extraStudy2Id = 4L;
        public static final Study newStudyWithDate = new Study(StudyTypeEnum.OTHER, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.now(), LocalDate.parse("2025-04-09"));
        public static final Study newStudyWithoutDate = new Study(StudyTypeEnum.OTHER, "simple image", Images.validImage, Users.patient, Users.patient, LocalDateTime.now(), LocalDate.now());
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

    public class DoctorDetails{
        public static final DoctorDetail doctorDetail = new DoctorDetail(Users.doctor, "med-licence", SpecialtyEnum.CARDIOLOGY);
    }

    public class DoctorCoverages{
        public static final DoctorCoverage doctorCoverage = new DoctorCoverage(Users.doctor, Insurances.validInsurance);
        public static final DoctorCoverage doctorCoverage2 = new DoctorCoverage(Users.doctor, Insurances.validInsurance2);
    }

    public class DoctorSingleShifts{
        public static final DoctorSingleShift doctorSingleShift = new DoctorSingleShift(Users.doctor, WeekdayEnum.THURSDAY, "Lavarden", LocalTime.parse("10:00:00"), LocalTime.parse("10:30:00"), 15);
        public static final Long doctorSingleShiftId = 1L;
    }

    public class NewAppointments{
        public static final AppointmentNew appointment = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2025-10-09"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"));
        public static final AppointmentNew appointment2 = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2025-10-09"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"));
        public static final AppointmentNew oldAppointment = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2025-04-09"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"));
        public static final AppointmentNew oldAppointment2 = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2025-04-09"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"));
        public static final AppointmentNew removedAppointment = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.patient, LocalDate.parse("2025-04-19"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"));
        public static final AppointmentNew removedAppointment2 = new AppointmentNew(DoctorSingleShifts.doctorSingleShift, Users.doctor, LocalDate.parse("2025-04-19"), LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"));
    }

    public class PatientDetails{
        public static final PatientDetail patientDetail = new PatientDetail(Users.patient, LocalDate.parse("2000-01-01"), null, 1.75, 89.00, null, null, null, null, null, null, null, "carpenter");
        public static final PatientDetail newPatientDetail = new PatientDetail(Users.patient, LocalDate.parse("2000-01-01"), BloodTypeEnum.AB_POSITIVE, 1.75, 89.00, false, true, null, null, null, null, null, null);
        public static final PatientDetail newPatientDetailNotNullValues = new PatientDetail(Users.patient, LocalDate.parse("2000-01-01"), BloodTypeEnum.AB_POSITIVE, 1.75, 89.00, false, true, "a", "a", "a", "a", "a", "a");
        public static final PatientDetail newPatientDetailNullValues = new PatientDetail(Users.patient, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public class AuthDoctors{
        public static final AuthDoctor authDoctor = new AuthDoctor(Users.doctor, Users.patient, AccessLevelEnum.VIEW_BASIC);
        public static final AuthDoctor authDoctorSocialLevel = new AuthDoctor(Users.doctor, Users.patient, AccessLevelEnum.VIEW_SOCIAL);
    }
}
