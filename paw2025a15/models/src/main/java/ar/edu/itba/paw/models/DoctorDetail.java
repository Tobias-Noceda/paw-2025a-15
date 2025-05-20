package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private User doctor;

    @Column(name = "doctor_licence", nullable = false)
    private String doctorLicense;

    @Enumerated
    @Column( name = "doctor_specialty", nullable = false)
    private SpecialtyEnum specialty;

    public DoctorDetail(){
        //just for hibernate
    }

    public DoctorDetail(User doctor, String doctorLicense, SpecialtyEnum specialty) {
        this.doctor = doctor;
        this.doctorLicense = doctorLicense;
        this.specialty = specialty;
    }

    public User getDoctor() {
        return doctor;
    }

    public String getDoctorLicense() {
        return doctorLicense;
    }

    public SpecialtyEnum getSpecialty() {
        return specialty;
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