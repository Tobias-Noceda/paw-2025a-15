package ar.edu.itba.paw.models;

public class AuthStudy {
    private final long doctorId;
    private final long studyId;

    public AuthStudy(long doctorId, long studyId){
        this.doctorId = doctorId;
        this.studyId = studyId;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public long getStudyId(){
        return studyId;
    }
}
