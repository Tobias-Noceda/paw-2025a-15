package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment {
    @EmbeddedId
    private AppointmentId id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "shift_id", referencedColumnName = "shift_id", nullable = false)
    private DoctorShift shift;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private User patient;

    @Column( name =  "appointment_date", nullable = false)
    private LocalDate date;

    public Appointment(){
        //just for hibernate
    }

    public Appointment(DoctorShift shift, User patient, LocalDate date){
        this.shift = shift;
        this.patient = patient;
        this.date = date;
        this.id = new AppointmentId(shift.getId(), date);
    }

    public AppointmentId getAppointmentId(){
        return id;
    }

    public DoctorShift getShift(){
        return shift;
    }

    public User getPatient(){
        return patient;
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

        return (this.id.equals(o.id)) &&
        (this.shift.equals(o.shift)) && (this.patient.equals(o.patient))
        && (this.date.equals(o.date));
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + shift.hashCode();
        result = 31 * result + patient.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "Appointment{" +
            "id=" + id +
            "," + "shift=" + shift +
            "," + "patient=" + patient +
            "," + "date=" + date +
            '}';
    }
}
