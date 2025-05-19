package ar.edu.itba.paw.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Column( name =  "shift_id")
    private final long shiftId;
    @Column( name =  "patient_id")
    private final long patientId;
    @Column( name =  "appointment_date")
    private final LocalDate date;

    public Appointment(long shiftId, long patientId, LocalDate date){
        this.shiftId = shiftId;
        this.patientId = patientId;
        this.date = date;
    }

    public long getShiftId(){
        return shiftId;
    }

    public long getPatientId(){
        return patientId;
    }

    public LocalDate getDate(){
        return date;
    }

    public String getDateNumber(){
        return String.format("%d", date.getDayOfMonth());
    }

    public String getDateMonthNumber() {
        return date.getMonth().toString();
    }

    public String getDateYearNumber() {
        return String.format("%d", date.getYear());
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof Appointment)) return false;

        Appointment o = (Appointment) other;

        return (this.shiftId==o.shiftId) && (this.patientId==o.patientId)
        && (this.date.equals(o.date));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(shiftId);
        result = 31 * result + Long.hashCode(patientId);
        result = 31 * result + date.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "Appointment{" +
            "shiftId=" + shiftId +
            "," + "patientId=" + patientId +
            "," + "date=" + date +
            '}';
    }
}
