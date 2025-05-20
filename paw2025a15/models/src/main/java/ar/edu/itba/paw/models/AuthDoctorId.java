package ar.edu.itba.paw.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

@Embeddable
public class AuthDoctorId implements Serializable{
    @Column(name = "doctor_id")
    private long doctorId;

    @Column(name = "patient_id")
    private long patientId;

    @Enumerated
    @Column(name = "access_level")
    private AccessLevelEnum accessLevel;

    public AuthDoctorId(){
        //just for hibernate
    }

    public AuthDoctorId(long doctorId, long patientId, AccessLevelEnum accessLevel){
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.accessLevel = accessLevel;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(long doctorId){
        this.doctorId = doctorId;
    }

    public long getPatientId(){
        return patientId;
    }

    public void setPatientId(long patientId){
        this.patientId = patientId;
    }

    public AccessLevelEnum getAccessLevel(){
        return accessLevel;
    }

    public void setAccessLevel(AccessLevelEnum accessLevel){
        this.accessLevel = accessLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthDoctorId)) return false;
        AuthDoctorId that = (AuthDoctorId) o;
        return Objects.equals(doctorId, that.doctorId) && Objects.equals(patientId, that.patientId)
        && Objects.equals(accessLevel, that.accessLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, patientId, accessLevel);
    }

    @Override
    public String toString(){
        return "AuthDoctorId{" +
            "doctorId=" + doctorId +
            ", patientId=" + patientId +
            ", accessLevel=" + accessLevel +
            '}';
    }
}
