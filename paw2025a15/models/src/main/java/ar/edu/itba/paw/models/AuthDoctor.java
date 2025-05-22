package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "auth_doctors")
public class AuthDoctor {
    @EmbeddedId
    private AuthDoctorId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private User doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private User patient;

    @Enumerated
    @Column( name = "access_level", nullable = false)
    private AccessLevelEnum accessLevel;

    public AuthDoctor(){
        //just for hibernate
    }

    public AuthDoctor(User doctor, User patient){
        this.doctor = doctor;
        this.patient = patient;
        this.accessLevel = AccessLevelEnum.VIEW_BASIC;
        this.id = new AuthDoctorId(doctor.getId(), patient.getId(), accessLevel);
    }

    public AuthDoctor(User doctor, User patient, AccessLevelEnum accessLevel){
        this.id = new AuthDoctorId(doctor.getId(), patient.getId(), accessLevel);
        this.doctor = doctor;
        this.patient = patient;
        this.accessLevel = accessLevel;
    }

    public AuthDoctorId getAuthDoctorId(){
        return id;
    }

    public User getDoctor(){
        return doctor;
    }

    public User getPatient(){
        return patient;
    }

    public AccessLevelEnum getAccessLevel(){
        return accessLevel;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof AuthDoctor)) return false;

        AuthDoctor o = (AuthDoctor) other;

        return (this.id.equals(o.id)) && (this.doctor.equals(o.doctor)) 
        && (this.patient.equals(o.patient)) && (this.accessLevel.ordinal() == o.accessLevel.ordinal());
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + doctor.hashCode();
        result = 31 * result + patient.hashCode();
        result = 31 * result + accessLevel.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "AuthDoctor{" +
            "id=" + id +
            ", doctor=" + doctor +
            ", patient=" + patient +
            ", accessLevel=" + accessLevel +
            '}';
    }
}
