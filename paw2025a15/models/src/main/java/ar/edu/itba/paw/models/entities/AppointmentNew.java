package ar.edu.itba.paw.models.entities;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments_new")
public class AppointmentNew {
    @EmbeddedId
    private AppointmentNewId id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("shiftId")
    @JoinColumn(name = "shift_id", referencedColumnName = "shift_id", nullable = false)
    private DoctorSingleShift shift;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private User patient;

    public AppointmentNew(){
        //just for hibernate
    }

    public AppointmentNew(DoctorSingleShift shift, User patient, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.shift = shift;
        this.patient = patient;
        this.id = new AppointmentNewId(shift.getId(), date, startTime, endTime);
    }

    public AppointmentNewId getId(){
        return id;
    }

    public void setId(AppointmentNewId id){
        this.id = id;
    }

    public DoctorSingleShift getShift(){
        return shift;
    }

    public void setShift(DoctorSingleShift shift){
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

        if(!(other instanceof AppointmentNew)) return false;

        AppointmentNew o = (AppointmentNew) other;

        return (this.id.equals(o.id));
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

