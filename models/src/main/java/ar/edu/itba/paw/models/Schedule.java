package ar.edu.itba.paw.models;

import java.util.List;

import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class Schedule {
    
    private List<WeekdayEnum> weekday;
    private String startTime;
    private String endTime;
    private String address;
    private int shiftCount;

    public Schedule(){
        //just for hibernate
    }
    public Schedule(List<WeekdayEnum> weekday, String startTime, String endTime, String address, int shiftCount) {
        this.weekday = weekday;
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
        this.shiftCount = shiftCount;
    }
    public List<WeekdayEnum> getWeekday() {
        return weekday;
    }

    public void setWeekday(List<WeekdayEnum> weekday) {
        this.weekday = weekday;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getShiftCount() {
        return shiftCount;
    }

    public void setShiftCount(int shiftCount) {
        this.shiftCount = shiftCount;
    }
}
