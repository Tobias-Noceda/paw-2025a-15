package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AuthStudyId implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "study_id")
    private Long studyId;

    public AuthStudyId(){
        //just for hibernate
    }

    public AuthStudyId(long doctorId, long studyId){
        this.doctorId = doctorId;
        this.studyId = studyId;
    }

    public Long getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(Long doctorId){
        this.doctorId = doctorId;
    }

    public Long getStudyId(){
        return studyId;
    }

    public void setStudyId(Long studyId){
        this.studyId = studyId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AuthStudyId)) return false;
        AuthStudyId o = (AuthStudyId) other;
        return (doctorId.equals(o.doctorId)) && (studyId.equals(o.studyId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, studyId);
    }

    @Override
    public String toString(){
        return "AuthStudyId{" +
            "doctorId=" + doctorId +
            ", studyId=" + studyId +
            '}';
    }
}
