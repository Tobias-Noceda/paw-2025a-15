package ar.edu.itba.paw.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "auth_studies")
public class AuthStudy {
    @EmbeddedId
    private AuthStudyId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private User doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_id", referencedColumnName = "study_id", nullable = false)
    private Study study;

    public AuthStudy(){
        //just for hibernate
    }

    public AuthStudy(User doctor, Study study){
        this.id = new AuthStudyId(doctor.getId(), study.getId());
        this.doctor = doctor;
        this.study = study;
    }

    public AuthStudyId getAuthStudyId(){
        return id;
    }

    public User getDoctor(){
        return doctor;
    }

    public Study getStudy(){
        return study;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof AuthStudy)) return false;

        AuthStudy o = (AuthStudy) other;

        return (this.id.equals(o.id)) && (this.doctor.equals(o.doctor)) 
        && (this.study.equals(o.study));
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + doctor.hashCode();
        result = 31 * result + study.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "AuthStudy{" +
            "id=" + id +
            ", doctor=" + doctor +
            ", study=" + study +
            '}';
    }
}
