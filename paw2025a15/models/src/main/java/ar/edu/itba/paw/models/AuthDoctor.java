package ar.edu.itba.paw.models;

public class AuthDoctor {
    private final long doctorId;
    private final long patientId;
    private final AccessLevelEnum accessLevel;

    public AuthDoctor(long doctorId, long patientId){
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.accessLevel = AccessLevelEnum.VIEW_BASIC;
    }

    public AuthDoctor(long doctorId, long patientId, AccessLevelEnum accessLevel){
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.accessLevel = accessLevel;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public long getPatientId(){
        return patientId;
    }

    public AccessLevelEnum getAccessLevel(){
        return accessLevel;
    }
}
