package ar.edu.itba.paw.interfaces.services;

import java.util.List;

public interface AuthStudiesService {

    public boolean authStudyForDoctorId(long studyId, long doctorId);

    public boolean authStudyListForDoctorId(List<Long> doctorsId, long studyId);

    public boolean hasAuthStudy(long studyId, long doctorId);

    public void unauthStudyForDoctorId(long studyId, long doctorId);

    public void toggleStudyForDoctorId(long studyId, long doctorId);

    public void unauthAllStudiesForDoctorIdAndPatientId(long userId, long doctorId);
}
