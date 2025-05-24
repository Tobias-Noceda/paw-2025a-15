package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AuthStudyId implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Column(name = "doctor_id")
    private long doctorId;

    @Column(name = "study_id")
    private long studyId;

    public AuthStudyId(){
        //just for hibernate
    }

    public AuthStudyId(long doctorId, long studyId){
        this.doctorId = doctorId;
        this.studyId = studyId;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(long doctorId){
        this.doctorId = doctorId;
    }

    public long getStudyId(){
        return studyId;
    }

    public void setStudyId(long studyId){
        this.studyId = studyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthStudyId)) return false;
        AuthStudyId that = (AuthStudyId) o;
        return (doctorId == that.doctorId) && (studyId == that.studyId);
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
