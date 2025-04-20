package ar.edu.itba.paw.models;

public class AuthDoctor {
    private final long doctorId;
    private final long patientId;

    public AuthDoctor(long doctorId, long patientId){
        this.doctorId = doctorId;
        this.patientId = patientId;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public long getPatientId(){
        return patientId;
    }
}
