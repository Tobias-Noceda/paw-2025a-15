package ar.edu.itba.paw.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "doctor_coverages")
public class DoctorCoverage {
    @Column(name = "doctor_id")
    private final long doctorId;
    @Column(name = "insurance_id")
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

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorCoverage)) return false;

        DoctorCoverage o = (DoctorCoverage) other;

        return (this.doctorId==o.doctorId) && (this.insuranceId==o.insuranceId);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(doctorId);
        result = 31 * result + Long.hashCode(insuranceId);
        return result;
    }

    @Override
    public String toString(){
        return "File{" +
            "doctorId=" + doctorId +
            "," + "insuranceId=" + insuranceId +
            '}';
    }
}
