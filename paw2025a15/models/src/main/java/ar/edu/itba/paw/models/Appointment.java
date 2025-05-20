package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment {

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "shift_id", referencedColumnName = "shift_id", nullable = false)
    private final long shiftId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private final long patientId;

    @Column( name =  "appointment_date", nullable = false)
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
