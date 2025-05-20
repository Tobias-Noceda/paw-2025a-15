package ar.edu.itba.paw.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DoctorCoverageId implements Serializable{
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorCoverageId)) return false;
        DoctorCoverageId that = (DoctorCoverageId) o;
        return Objects.equals(doctorId, that.doctorId) && Objects.equals(insuranceId, that.insuranceId);
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
