package ar.edu.itba.paw.models;

import java.time.LocalDate;

public class AvailableTurn {
    private final LocalDate date;
    private final String timeRange;
    private final String address;
    private final long shiftId;
    private final int index;
    
    public AvailableTurn(LocalDate date, String timeRange, String address, long shiftId, int index) {
        this.date = date;
        this.timeRange = timeRange;
        this.address = address;
        this.shiftId = shiftId;
        this.index = index;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public String getAddress() {
        return address;
    }

    public long getShiftId() {
        return shiftId;
    }

    public int getIndex() {
        return index;
    }

    public String getStartTime() {
        return timeRange.substring(13, 18);
    }

    public String getEndTime() {
        return timeRange.substring(35, 40);
    }
}
