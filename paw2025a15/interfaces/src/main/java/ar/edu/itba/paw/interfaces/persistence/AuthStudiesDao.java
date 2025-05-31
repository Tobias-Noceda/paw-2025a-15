package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

public interface AuthStudiesDao {

    public boolean authStudyForDoctorId(long studyId, long doctorId);

    public boolean hasAuthStudy(long studyId, long doctorId);

    public void authStudyForDoctorIdList(List<Long> doctorsId, long StudyId);

    public void unauthStudyForDoctorId(long studyId, long doctorId);

    public void unauthAllStudiesForDoctorIdAndPatientId(long userId, long doctorId);
}
