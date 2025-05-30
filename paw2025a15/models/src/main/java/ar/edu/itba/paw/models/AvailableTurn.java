package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailableTurn {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String address;
    private long shiftId;

    public AvailableTurn(){
        //just for hibernate
    }
    
    public AvailableTurn(LocalDate date, LocalTime startTime, LocalTime endTime, String address, long shiftId) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
        this.shiftId = shiftId;
    }

    public AvailableTurn(LocalTime startTime, LocalTime endTime, String address, long shiftId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
        this.shiftId = shiftId;
    }

    public LocalDate getDate() {
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

    public long getShiftId() {
        return shiftId;
    }

    public String getStartToEndTime() {
        return getStartTime() + " - " + getEndTime();
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof AvailableTurn)) return false;

        AvailableTurn o = (AvailableTurn) other;

        return (this.date.equals(o.date)) && (this.startTime.equals(o.startTime))
        && (this.endTime.equals(o.endTime)) && (this.address.equals(o.address))
        && (this.shiftId==o.shiftId);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(shiftId);
        result = 31 * result + date.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "AvailableTurn{" +
            "shiftId=" + shiftId +
            "," + "date=" + date +
            "," + "address=" + address +
            "," + "startTime=" + startTime +
            "," + "endTime=" + endTime +
            '}';
    }
}
