package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import ar.edu.itba.paw.webapp.form.constraints.AvailableTurn;

@AvailableTurn
public class TakeTurnForm {
    @NotNull
    private int shiftId;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private Long doctorId;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Size(max = 500)
    private String detail;

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDetail() { return detail; }

    public void setDetail(String detail) { this.detail = detail; }
}