package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

public interface AuthStudiesDao {

    public boolean authStudyForDoctor(long studyId, long doctorId);

    public boolean hasAuthStudy(long studyId, long doctorId);

    public void authStudyForDoctorIdList(List<Long> doctorsId, long studyId);

    public void unauthStudyForDoctor(long studyId, long doctorId);

    public void unauthAllStudiesForDoctorAndPatient(long patientId, long doctorId);

    public void unauthAllStudiesForAllDocsForPatientId(long patientId);

    public void authStudyForAllAuthDoctors(long studyId);

    public void deauthStudyForAllDoctors(long studyId);
}
