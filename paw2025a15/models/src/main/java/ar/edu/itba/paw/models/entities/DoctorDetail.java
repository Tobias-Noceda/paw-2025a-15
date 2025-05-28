package ar.edu.itba.paw.models.entities;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table( name = "doctor_details",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "doctor_licence")}
)
public class DoctorDetail {
    @Id
    private Long doctorId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private User doctor;

    @Column(name = "doctor_licence", length = 50 ,nullable = false)
    private String doctorLicense;

    @Enumerated(EnumType.ORDINAL)
    @Column( name = "doctor_specialty", nullable = false)
    private SpecialtyEnum specialty;

    public DoctorDetail(){
        //just for hibernate
    }

    public DoctorDetail(User doctor, String doctorLicense, SpecialtyEnum specialty) {
        this.doctorId = doctor.getId();
        this.doctor = doctor;
        this.doctorLicense = doctorLicense;
        this.specialty = specialty;
    }

    public Long getDoctorId(){
        return doctorId;
    }

    public void setDoctorId(Long doctorId){
        this.doctorId = doctorId;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor){
        this.doctor = doctor;
        this.doctorId = doctor.getId();
    }

    public String getDoctorLicense() {
        return doctorLicense;
    }

    public void setDoctorLicense(String doctorLicense){
        this.doctorLicense = doctorLicense;
    }

    public SpecialtyEnum getSpecialty() {
        return specialty;
    }

    public void setSpecialty(SpecialtyEnum specialty){
        this.specialty = specialty;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (!(other instanceof DoctorDetail)) return false;

        DoctorDetail o = (DoctorDetail) other;

        return (this.doctor.equals(o.doctor))
                && (this.doctorLicense.equals(o.doctorLicense))
                && (this.specialty.equals(o.specialty));
    }

    @Override
    public int hashCode() {
        int result = doctor.hashCode();
        result = 31 * result + doctorLicense.hashCode();
        result = 31 * result + specialty.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DoctorDetail{" +
                "doctor=" + doctor +
                ", doctorLicense='" + doctorLicense + '\'' +
                ", specialty=" + specialty +
                '}';
    }
}