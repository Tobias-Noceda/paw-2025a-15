package ar.edu.itba.paw.interfaces.services;

public interface AuthStudiesService {

    public boolean authStudyForDoctorId(long studyId, long doctorId);

    public boolean hasAuthStudy(long studyId, long doctorId);

    public void unauthStudyForDoctorId(long studyId, long doctorId);

    public void toggleStudyForDoctorId(long studyId, long doctorId);

    public void unauthAllStudiesForDoctorIdAndPatientId(long userId, long doctorId);
}
