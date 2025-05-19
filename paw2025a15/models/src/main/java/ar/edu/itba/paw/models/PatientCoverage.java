package ar.edu.itba.paw.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "insurance_coverages")
public class PatientCoverage {
    @Column(name = "patient_id")
    private final long patientId;
    @Column(name = "insurance_id")
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
