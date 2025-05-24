package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentData {
    private long shiftId;
    private long patientId;
    private String patientName;
    private long doctorId;
    private String doctorName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String address;

    public AppointmentData(){
        //just for hibernate
    }

    public AppointmentData(long shiftId, long patientId, String patientName, long doctorId, String doctorName, LocalDate date, LocalTime startTime, LocalTime endTime, String address){
        this.shiftId = shiftId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
    }

    public long getShiftId(){
        return shiftId;
    }

    public long getPatientId(){
        return patientId;
    }

    public String getPatientName(){
        return patientName;
    }

    public long getDoctorId(){
        return doctorId;
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

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof AppointmentData)) return false;

        AppointmentData o = (AppointmentData) other;

        return (this.shiftId==o.shiftId) 
        && (this.patientName.equals(o.patientName))
        && (this.doctorName.equals(o.doctorName))
        && (this.date.equals(o.date))
        && (this.startTime.equals(o.startTime))
        && (this.endTime.equals(o.endTime))
        && (this.address.equals(o.address));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(shiftId);
        result = 31 * result + patientName.hashCode();
        result = 31 * result + doctorName.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "AppointmentData{" +
            "shiftId=" + shiftId +
            "," + "patientName=" + patientName +
            "," + "doctorName=" + doctorName +
            "," + "date=" + date +
            "," + "startTime=" + startTime +
            "," + "endTime=" + endTime +
            "," + "address=" + address +
            '}';
    }
}
