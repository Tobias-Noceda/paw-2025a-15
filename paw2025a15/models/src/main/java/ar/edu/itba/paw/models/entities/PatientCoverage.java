package ar.edu.itba.paw.models.entities;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "patient_coverages")
public class PatientCoverage {
    @Id
    private Long patientId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", referencedColumnName = "insurance_id", nullable = false)
    private Insurance insurance;

    public PatientCoverage(){
        //just for hibernate
    }

    public PatientCoverage(User patient, Insurance insurance){
        this.patientId = patient.getId();
        this.patient = patient;
        this.insurance = insurance;
    }

    public Long getPatientId(){
        return patientId;
    }

    public void setPatientId(Long patientId){
        this.patientId = patientId;
    }

    public User getPatient(){
        return patient;
    }

    public void setPatient(User patient){
        this.patient = patient;
        this.patientId = patient.getId();
    }

    public Insurance getInsurance(){
        return insurance;
    }

    public void setInsurance(Insurance insurance){
        this.insurance = insurance;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof PatientCoverage)) return false;

        PatientCoverage o = (PatientCoverage) other;

        return (this.patientId==o.patientId);
    }

    @Override
    public int hashCode() {
        int result = patient.hashCode();
        result = 31 * result + insurance.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "PatientCoverage{" +
            "patient=" + patient +
            "," + "insurance=" + insurance +
            '}';
    }

}
