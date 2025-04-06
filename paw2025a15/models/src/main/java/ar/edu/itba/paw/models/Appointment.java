package ar.edu.itba.paw.models;

import java.time.LocalDate;

public class Appointment {
    private final long shiftId;
    private final long patientId;
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
}
