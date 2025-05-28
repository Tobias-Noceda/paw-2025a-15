package ar.edu.itba.paw.models.entities;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "auth_doctors")
public class AuthDoctor {
    @EmbeddedId
    private AuthDoctorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id")
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id") 
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    public AuthDoctor(){
        //just for hibernate
    }

    public AuthDoctor(Doctor doctor, Patient patient){
        this.doctor = doctor;
        this.patient = patient;
        this.id = new AuthDoctorId(doctor.getId(), patient.getId(), AccessLevelEnum.VIEW_BASIC);
    }

    public AuthDoctor(Doctor doctor, Patient patient, AccessLevelEnum accessLevel){
        this.id = new AuthDoctorId(doctor.getId(), patient.getId(), accessLevel);
        this.doctor = doctor;
        this.patient = patient;
    }

    public AuthDoctorId getAuthDoctorId(){
        return id;
    }

    public void setAuthDoctorId(AuthDoctorId id){
        this.id = id;
    }

    public Doctor getDoctor(){
        return doctor;
    }

    public void setDoctor(Doctor doctor){
        this.doctor = doctor;
        this.id.setDoctorId(doctor.getId());
    }

    public Patient getPatient(){
        return patient;
    }

    public void setPatient(Patient patient){
        this.patient = patient;
        this.id.setPatientId(patient.getId());
    }

    public AccessLevelEnum getAccessLevel(){
        return id.getAccessLevel();
    }

    public void setAccessLevel(AccessLevelEnum accessLevel){
        this.id.setAccessLevel(accessLevel);
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof AuthDoctor)) return false;

        AuthDoctor o = (AuthDoctor) other;

        return (this.id.equals(o.id)) && (this.doctor.equals(o.doctor)) 
        && (this.patient.equals(o.patient));
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + doctor.hashCode();
        result = 31 * result + patient.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "AuthDoctor{" +
            "id=" + id +
            ", doctor=" + doctor +
            ", patient=" + patient +
            '}';
    }
}
