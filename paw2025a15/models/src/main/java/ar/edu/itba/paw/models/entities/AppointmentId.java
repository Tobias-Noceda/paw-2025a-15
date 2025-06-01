package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AppointmentId implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Column(name = "shift_id")
    private Long shiftId;

    @Column(name = "appointment_date")
    private LocalDate date;

    public AppointmentId(){
        //just for hibernate;
    }

    public AppointmentId(long shiftId, LocalDate date){
        this.shiftId = shiftId;
        this.date = date;
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

    public void setDate(LocalDate date){
        this.date = date;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AppointmentId)) return false;
        AppointmentId o = (AppointmentId) other;
        return (this.shiftId.equals(o.shiftId)) 
        && Objects.equals(this.date, o.date);
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
