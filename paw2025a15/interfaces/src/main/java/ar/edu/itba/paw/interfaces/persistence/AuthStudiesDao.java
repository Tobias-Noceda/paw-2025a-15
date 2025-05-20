package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.User;

public interface AuthStudiesDao {

    public boolean authStudyForDoctorId(Study study, User doctor);

    public boolean hasAuthStudy(long studyId, long doctorId);

    public void unauthStudyForDoctorId(long studyId, long doctorId);

    public void unauthAllStudiesForDoctorIdAndPatientId(long userId, long doctorId);
}
