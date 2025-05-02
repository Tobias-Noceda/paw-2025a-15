package ar.edu.itba.paw.models;

import java.time.LocalTime;

import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class DoctorShift {
    private final long id;
    private final long doctorId;
    private final WeekdayEnum weekday;
    private final String address;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public DoctorShift(long id, long doctorId, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime){
        this.id = id;
        this.doctorId = doctorId;
        this.weekday = weekday;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId(){
        return id;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public WeekdayEnum getWeekday(){
        return weekday;
    }

    public String getAddress(){
        return address;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
