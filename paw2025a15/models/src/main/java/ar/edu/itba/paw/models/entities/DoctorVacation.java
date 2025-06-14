package ar.edu.itba.paw.models.entities;

import java.time.LocalDate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "doctor_vacations")
public class DoctorVacation {

    @EmbeddedId
    private DoctorVacationId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("doctorId")
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    private Doctor doctor;

    public DoctorVacation() {
        // Default constructor for Hibernate
    }

    public DoctorVacation(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        this.doctor = doctor;
        this.id = new DoctorVacationId(doctor.getId(), startDate, endDate);
    }

    public DoctorVacationId getId() {
        return id;
    }

    public void setId(DoctorVacationId id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorVacation)) return false;

        DoctorVacation that = (DoctorVacation) o;

        if (!id.equals(that.id)) return false;
        return doctor.equals(that.doctor);   
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + doctor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DoctorVacation{" +
                "id=" + id +
                ", doctor=" + doctor +
                '}';
    }
}
