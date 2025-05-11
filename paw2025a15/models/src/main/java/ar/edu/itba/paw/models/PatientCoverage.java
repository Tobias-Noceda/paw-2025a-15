package ar.edu.itba.paw.models;

public class PatientCoverage {
    private final long patientId;
    private final long insuranceId;

    public PatientCoverage(long patientId, long insuranceId){
        this.patientId = patientId;
        this.insuranceId = insuranceId;
    }

    public long getPatientId(){
        return patientId;
    }

    public long getInsuranceId(){
        return insuranceId;
    }

}
