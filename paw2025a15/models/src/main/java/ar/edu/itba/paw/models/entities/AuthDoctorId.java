package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

@Embeddable
public class AuthDoctorId implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "patient_id")
    private Long patientId;

    @Enumerated(EnumType.ORDINAL)
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

    public Long getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(Long doctorId){
        this.doctorId = doctorId;
    }

    public Long getPatientId(){
        return patientId;
    }

    public void setPatientId(Long patientId){
        this.patientId = patientId;
    }

    public AccessLevelEnum getAccessLevel(){
        return accessLevel;
    }

    public void setAccessLevel(AccessLevelEnum accessLevel){
        this.accessLevel = accessLevel;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AuthDoctorId)) return false;
        AuthDoctorId o = (AuthDoctorId) other;
        return (doctorId.equals(o.doctorId)) && (patientId.equals(o.patientId))
        && (accessLevel.ordinal() == o.accessLevel.ordinal());
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
