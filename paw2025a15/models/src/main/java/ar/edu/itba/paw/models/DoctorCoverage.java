package ar.edu.itba.paw.models;

public class DoctorCoverage {
    private final long doctorId;
    private final long insuranceId;

    public DoctorCoverage(long doctorId, long insuranceId){
        this.doctorId = doctorId;
        this.insuranceId = insuranceId;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public long getInsuranceId(){
        return insuranceId;
    }
}
