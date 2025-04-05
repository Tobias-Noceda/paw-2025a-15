package ar.edu.itba.paw.models;

public class DoctorDetail {
    private final long doctorId;
    private final String licence;
    private final String specialty;

    public DoctorDetail(long doctorId, String licence, String specialty){
        this.doctorId = doctorId;
        this.licence = licence;
        this.specialty = specialty;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public String getLicence(){
        return licence;
    }

    public String getSpecialty(){
        return specialty;
    }
}
