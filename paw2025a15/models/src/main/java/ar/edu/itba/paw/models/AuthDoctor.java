package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "auth_doctors")
public class AuthDoctor {
    @Column(name = "doctor_id")
    private final long doctorId;
    @Column(name = "patient_id")
    private final long patientId;
    @Column( name = "access_level")
    private final AccessLevelEnum accessLevel;

    public AuthDoctor(long doctorId, long patientId){
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.accessLevel = AccessLevelEnum.VIEW_BASIC;
    }

    public AuthDoctor(long doctorId, long patientId, AccessLevelEnum accessLevel){
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.accessLevel = accessLevel;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public long getPatientId(){
        return patientId;
    }

    public AccessLevelEnum getAccessLevel(){
        return accessLevel;
    }
}
