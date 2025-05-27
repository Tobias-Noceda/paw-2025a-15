package ar.edu.itba.paw.models.entities;

import javax.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment {
    @EmbeddedId
    private AppointmentId id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @MapsId("shiftId") 
    @JoinColumn(name = "shift_id", referencedColumnName = "shift_id", nullable = false)
    private DoctorShift shift;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private User patient;

    public Appointment(){
        //just for hibernate
    }

    public Appointment(DoctorShift shift, User patient, LocalDate date){
        this.shift = shift;
        this.patient = patient;
        this.id = new AppointmentId(shift.getId(), date);
    }

    public AppointmentId getAppointmentId(){
        return id;
    }

    public void setAppointmentId(AppointmentId id){
        this.id = id;
    }

    public DoctorShift getShift(){
        return shift;
    }

    public void setShift(DoctorShift shift){
        this.shift = shift;
        this.id.setShiftId(shift.getId());
    }

    public User getPatient(){
        return patient;
    }

    public void setPatient(User patient){
        this.patient = patient;
    }

    public LocalDate getDate(){
        return id.getDate();
    }

    public void setDate(LocalDate date){
        this.id.setDate(date);
    }

    public String getDateNumber(){
        return String.format("%d", getDate().getDayOfMonth());
    }

    public String getDateMonthNumber() {
        return getDate().getMonth().toString();
    }

    public String getDateYearNumber() {
        return String.format("%d", getDate().getYear());
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof Appointment)) return false;

        Appointment o = (Appointment) other;

        return (this.id.equals(o.id))
        && (this.shift.equals(o.shift)) 
        && (this.patient.equals(o.patient));
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + shift.hashCode();
        result = 31 * result + patient.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "Appointment{" +
            "id=" + id +
            "," + "shift=" + shift +
            "," + "patient=" + patient +
            '}';
    }
}
