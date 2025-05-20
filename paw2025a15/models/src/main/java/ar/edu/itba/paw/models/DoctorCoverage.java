package ar.edu.itba.paw.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "doctor_coverages")
public class DoctorCoverage {
    @EmbeddedId
    private DoctorCoverageId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private User doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insurance_id", referencedColumnName = "insurance_id", nullable = false)
    private Insurance insurance;

    public DoctorCoverage(){
        //just for hibernate
    }

    public DoctorCoverage(User doctor, Insurance insurance){
        this.id = new DoctorCoverageId(doctor.getId(), insurance.getId());
        this.doctor = doctor;
        this.insurance = insurance;
    }

    public DoctorCoverageId getDoctorCoverageId(){
        return id;
    }

    public User getDoctor(){
        return doctor;
    }

    public Insurance getInsurance(){
        return insurance;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorCoverage)) return false;

        DoctorCoverage o = (DoctorCoverage) other;

        return (this.doctor.equals(o.doctor)) && (this.insurance.equals(o.insurance));
    }

    @Override
    public int hashCode() {
        int result = doctor.hashCode();
        result = 31 * result + insurance.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "DoctorCoverage{" +
            "doctor=" + doctor +
            "," + "insurance=" + insurance +
            '}';
    }
}
