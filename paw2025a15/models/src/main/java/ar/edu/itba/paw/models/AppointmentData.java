package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentData {
    private final long shiftId;
    private final String patientName;
    private final String doctorName;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String address;

    public AppointmentData(long shiftId, String patientName, String doctorName, LocalDate date, LocalTime startTime, LocalTime endTime, String address){
        this.shiftId = shiftId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
    }

    public long getShiftId(){
        return shiftId;
    }

    public String getPatientName(){
        return patientName;
    }

    public String getDoctorName(){
        return doctorName;
    }

    public LocalDate getDate(){
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getAddress() {
        return address;
    }

    public String getStartToEndTime() {
        return getStartTime() + " - " + getEndTime();
    }
}
