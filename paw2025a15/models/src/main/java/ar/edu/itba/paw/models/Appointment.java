package ar.edu.itba.paw.models;

import java.util.Date;

public class Appointment {
    private final long shiftId;
    private final long patientId;
    private final int idx;
    private final Date date;

    public Appointment(long shiftId, long patientId, int idx, Date date){
        this.shiftId = shiftId;
        this.patientId = patientId;
        this.idx = idx;
        this.date = date;
    }

    public long getShiftId(){
        return shiftId;
    }

    public long getPatientId(){
        return patientId;
    }

    public int getIdx(){
        return idx;
    }

    public Date getDate(){
        return date;
    }
}
