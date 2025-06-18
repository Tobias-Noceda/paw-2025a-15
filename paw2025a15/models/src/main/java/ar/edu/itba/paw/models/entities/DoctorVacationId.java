package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DoctorVacationId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "vacation_start_date")
    private LocalDate startDate;

    @Column(name = "vacation_end_date")
    private LocalDate endDate;

    public DoctorVacationId() {
        // Default constructor for Hibernate
    }

    public DoctorVacationId(Long doctorId, LocalDate startDate, LocalDate endDate) {
        this.doctorId = doctorId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorVacationId)) return false;

        DoctorVacationId that = (DoctorVacationId) o;

        if (!doctorId.equals(that.doctorId)) return false;
        if (!startDate.equals(that.startDate)) return false;
        return endDate.equals(that.endDate);
    }

    @Override
    public int hashCode() {
        int result = doctorId.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DoctorVacationId{" +
                "doctorId=" + doctorId +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}