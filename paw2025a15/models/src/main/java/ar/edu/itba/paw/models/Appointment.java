package ar.edu.itba.paw.models;

import java.util.Date;

public class Appointment {
    private final long shiftId;
    private final long patientId;
    private final Date date;

    public Appointment(long shiftId, long patientId, Date date){
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

    public Date getDate(){
        return date;
    }
}
