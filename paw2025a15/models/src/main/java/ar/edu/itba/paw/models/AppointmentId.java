package ar.edu.itba.paw.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AppointmentId implements Serializable{
    @Column(name = "shift_id")
    private long shiftId;

    @Column(name = "appointment_date")
    private LocalDate date;

    public AppointmentId(){
        //just for hibernate;
    }

    public AppointmentId(long shiftId, LocalDate date){
        this.shiftId = shiftId;
        this.date = date;
    }

    public long getShiftId(){
        return shiftId;
    }

    public void setDoctorId(long shiftId){
        this.shiftId = shiftId;
    }

    public LocalDate getDate(){
        return date;
    }

    public void setInsuranceId(LocalDate date){
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentId)) return false;
        AppointmentId that = (AppointmentId) o;
        return Objects.equals(shiftId, that.shiftId) && Objects.equals(date, that.date);
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
