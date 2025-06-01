package ar.edu.itba.paw.models.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

// TODO: revisar si vale la pena. Se usa solo en tests, por ahi se puede actualizar tests y borrar
@Entity
@Table(name = "doctor_coverages")
public class DoctorCoverage {
    @EmbeddedId
    private DoctorCoverageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id")
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id") 
    @JoinColumn(name = "insurance_id", referencedColumnName = "insurance_id", nullable = false)
    private Insurance insurance;

    public DoctorCoverage(){
        //just for hibernate
    }

    public DoctorCoverage(Doctor doctor, Insurance insurance){
        this.id = new DoctorCoverageId(doctor.getId(), insurance.getId());
        this.doctor = doctor;
        this.insurance = insurance;
    }

    public DoctorCoverageId getDoctorCoverageId(){
        return id;
    }

    public void setDoctorCoverageId(DoctorCoverageId id){
        this.id = id;
    }

    public User getDoctor(){
        return doctor;
    }

    public void setDoctor(Doctor doctor){
        this.doctor = doctor;
        this.id.setDoctorId(doctor.getId());
    }

    public Insurance getInsurance(){
        return insurance;
    }

    public void setInsurance(Insurance insurance){
        this.insurance = insurance;
        this.id.setInsuranceId(insurance.getId());
    }

    public String getInsuranceName(){
        return insurance.getName();
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorCoverage)) return false;

        DoctorCoverage o = (DoctorCoverage) other;

        return (this.id.equals(o.id));
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
