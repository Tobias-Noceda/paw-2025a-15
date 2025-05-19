package ar.edu.itba.paw.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "auth_studies")
public class AuthStudy {
    @Column(name = "doctor_id")
    private final long doctorId;
    @Column(name = "study_id")
    private final long studyId;

    public AuthStudy(long doctorId, long studyId){
        this.doctorId = doctorId;
        this.studyId = studyId;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public long getStudyId(){
        return studyId;
    }
}
