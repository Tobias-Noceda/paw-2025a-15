package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AppointmentNewId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Column(name = "shift_id")
    private Long shiftId;

    @Column(name = "appointment_date")
    private LocalDate date;

    @Column(name = "appointment_start_time")
    private LocalTime startTime;

    @Column(name = "appointment_end_time")
    private LocalTime endTime;

    public AppointmentNewId(){
        //just for hibernate;
    }

    public AppointmentNewId(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime){ 
        this.shiftId = shiftId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getShiftId(){
        return shiftId;
    }

    public void setShiftId(Long shiftId){
        this.shiftId = shiftId;
    }

    public LocalDate getDate(){
        return date;
    }

    public Date getDateAsDate() {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void setDate(LocalDate date){
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

    public String getStartToEndTime() {
        return startTime + " - " + endTime;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AppointmentNewId)) return false;
        AppointmentNewId o = (AppointmentNewId) other;
        return (shiftId.equals(o.shiftId)) && Objects.equals(date, o.date)
            && Objects.equals(startTime, o.startTime)
            && Objects.equals(endTime, o.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId, date);
    }

    @Override
    public String toString(){
        return "AppointmentId{" +
            "shiftId=" + shiftId +
            ", date=" + date +
            '}';
    }
    
}
