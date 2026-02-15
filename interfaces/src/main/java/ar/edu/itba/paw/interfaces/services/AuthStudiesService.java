package ar.edu.itba.paw.interfaces.services;

import java.util.List;

public interface AuthStudiesService {//TODO check deprecated

    public boolean authStudyForDoctorId(long studyId, long doctorId);

    public void authStudyForDoctorIdList(List<Long> doctorsId, long studyId);
    
    public void unauthStudyForDoctorIdList(List<Long> doctorsId, long studyId);

    public boolean hasAuthStudy(long studyId, long doctorId);

    @Deprecated
    public void unauthStudyForDoctorId(long studyId, long doctorId);

    @Deprecated
    public void toggleStudyForDoctorId(long studyId, long doctorId);

    public void unauthAllStudiesForDoctorIdAndPatientId(long patientId, long doctorId);
    
    @Deprecated
    public void authorizeAllDoctorsForStudy(long studyId);
    
    @Deprecated
    public void deauthorizeAllDoctorsForStudy(long studyId);

    public void unauthAllStudiesForAllDocsForPatientId(long patientId);
}
