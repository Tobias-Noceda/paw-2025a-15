package ar.edu.itba.paw.models;

public class Schedule {
    
    private final WeekdayEnum weekday;
    private final String startTime;
    private final String endTime;
    private final String address;
    private final int shiftCount;

    public Schedule(
        final WeekdayEnum weekday,
        final String startTime,
        final String endTime,
        final String address,
        final int shiftCount
    ) {
        this.weekday = weekday;
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
        this.shiftCount = shiftCount;
    }

    public WeekdayEnum getWeekday() {
        return weekday;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getAddress() {
        return address;
    }

    public int getShiftCount() {
        return shiftCount;
    }
}
