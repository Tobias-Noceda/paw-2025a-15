package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailableTurn {
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String address;
    private final long shiftId;
    
    public AvailableTurn(LocalDate date, LocalTime startTime, LocalTime endtTime, String address, long shiftId) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endtTime;
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
}
