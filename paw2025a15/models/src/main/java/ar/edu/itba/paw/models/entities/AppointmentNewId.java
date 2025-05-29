package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AppointmentNewId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Column(name = "shift_id")
    private long shiftId;

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

    public long getShiftId(){
        return shiftId;
    }

    public void setShiftId(long shiftId){
        this.shiftId = shiftId;
    }

    public LocalDate getDate(){
        return date;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentNewId)) return false;
        AppointmentNewId that = (AppointmentNewId) o;
        return (shiftId == that.shiftId) && Objects.equals(date, that.date)
            && Objects.equals(startTime, that.startTime)
            && Objects.equals(endTime, that.endTime);
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
