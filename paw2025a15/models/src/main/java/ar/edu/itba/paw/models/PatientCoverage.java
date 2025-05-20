package ar.edu.itba.paw.models;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "insurance_coverages")
public class PatientCoverage {
    @Id
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insurance_id", referencedColumnName = "insurance_id", nullable = false)
    private Insurance insurance;

    public PatientCoverage(){
        //just for hibernate
    }

    public PatientCoverage(User patient, Insurance insurance){
        this.patient = patient;
        this.insurance = insurance;
    }

    public User getPatient(){
        return patient;
    }

    public Insurance getInsurance(){
        return insurance;
    }

}
