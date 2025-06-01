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
    private long doctorId;

    @Column(name = "insurance_id")
    private long insuranceId;

    public DoctorCoverageId(){
        //just for hibernate;
    }

    public DoctorCoverageId(long doctorId, long insuranceId){
        this.doctorId = doctorId;
        this.insuranceId = insuranceId;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(long doctorId){
        this.doctorId = doctorId;
    }

    public long getInsuranceId(){
        return insuranceId;
    }

    public void setInsuranceId(long insuranceId){
        this.insuranceId = insuranceId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof DoctorCoverageId)) return false;
        DoctorCoverageId o = (DoctorCoverageId) other;
        return (this.doctorId == o.doctorId) 
        && (this.insuranceId == o.insuranceId);
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
