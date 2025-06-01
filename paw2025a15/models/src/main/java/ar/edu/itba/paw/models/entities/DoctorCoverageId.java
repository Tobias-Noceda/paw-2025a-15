package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

// TODO: borrar si se borra DoctorCoverage
@Embeddable
public class DoctorCoverageId implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "insurance_id")
    private Long insuranceId;

    public DoctorCoverageId(){
        //just for hibernate;
    }

    public DoctorCoverageId(long doctorId, long insuranceId){
        this.doctorId = doctorId;
        this.insuranceId = insuranceId;
    }

    public Long getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(Long doctorId){
        this.doctorId = doctorId;
    }

    public Long getInsuranceId(){
        return insuranceId;
    }

    public void setInsuranceId(Long insuranceId){
        this.insuranceId = insuranceId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof DoctorCoverageId)) return false;
        DoctorCoverageId o = (DoctorCoverageId) other;
        return (this.doctorId.equals(o.doctorId)) 
        && (this.insuranceId.equals(o.insuranceId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, insuranceId);
    }

    @Override
    public String toString(){
        return "DoctorCoverageId{" +
            "doctorId=" + doctorId +
            ", insuranceId=" + insuranceId +
            '}';
    }
    
}
