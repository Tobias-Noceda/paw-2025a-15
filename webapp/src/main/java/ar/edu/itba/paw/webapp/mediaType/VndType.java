package ar.edu.itba.paw.webapp.mediaType;

public class VndType {

    public VndType() {
        throw new AssertionError();
    }

    // Appointment Media Types
    public static final String APPLICATION_APPOINTMENT = "application/vnd.appointments.v1+json";

    // Doctor Media Types
    public static final String APPLICATION_DOCTOR = "application/vnd.doctors.v1+json";

    public static final String APPLICATION_DOCTOR_CREATION = "application/vnd.doctors.creation.v1+json";

    public static final String APPLICATION_DOCTOR_SHIFT = "application/vnd.doctors.shifts.v1+json";

    public static final String APPLICATION_DOCTOR_AUTHORIZATION = "application/vnd.doctors.authorizations.v1+json";

    public static final String APPLICATION_DOCTOR_VACATIONS = "application/vnd.doctors.vacations.v1+json";

    // Insurance Media Types
    public static final String APPLICATION_INSURANCE = "application/vnd.insurances.v1+json";

    public static final String APPLICATION_INSURANCE_CREATION = "application/vnd.insurances.creation.v1+json";

    // Patient Media Types
    public static final String APPLICATION_PATIENT = "application/vnd.patients.v1+json";

    public static final String APPLICATION_PATIENT_CREATION = "application/vnd.patients.creation.v1+json";

    public static final String APPLICATION_PATIENT_SOCIALINFO = "application/vnd.patients.socialInfo.v1+json";

    public static final String APPLICATION_PATIENT_HABITSINFO = "application/vnd.patients.habitsInfo.v1+json";

    public static final String APPLICATION_PATIENT_MEDICALINFO = "application/vnd.patients.medicalInfo.v1+json";

    public static final String APPLICATION_PATIENT_STUDY = "application/vnd.patients.studies.v1+json";

    // Files Media Types
    public static final String APPLICATION_FILE = "application/vn.files.v1+json";

    // Password resets Media Types
    public static final String APPLICATION_PASSWORD_RESET = "application/vnd.user-password.reset.v1+json";

    public static final String APPLICATION_PASSWORD_CREATE = "application/vnd.user-password.creation.v1+json";
}
