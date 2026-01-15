package ar.edu.itba.paw.webapp.dto;

import java.net.URI;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class ShiftDTO {
    private WeekdayEnum weekday;
    private String address;
    private String startTime;
    private String endTime;
    private int duration;

    private URI self;
    private URI doctor;

    public static Function<DoctorSingleShift, ShiftDTO> mapper(final UriInfo uriInfo) {
        return s -> fromShift(s, uriInfo);
    }

    public static ShiftDTO fromShift(DoctorSingleShift shift, UriInfo uriInfo) {
        final ShiftDTO dto = new ShiftDTO();

        dto.weekday = shift.getWeekday();
        dto.address = shift.getAddress();
        dto.startTime = shift.getStartTime().toString();
        dto.endTime = shift.getEndTime().toString();
        dto.duration = shift.getDuration();

        dto.self = uriInfo.getBaseUriBuilder().path("shifts").path(String.valueOf(shift.getId())).build();
        dto.doctor = uriInfo.getBaseUriBuilder().path("doctors").path(String.valueOf(shift.getDoctor().getId())).build();
        
        return dto;
    }

    // Getters
    public WeekdayEnum getWeekday() {
        return weekday;
    }

    public String getAddress() {
        return address;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return duration;
    }

    public URI getSelf() {
        return self;
    }

    public URI getDoctor() {
        return doctor;
    }

    // Setters
    public void setWeekday(WeekdayEnum weekday) {
        this.weekday = weekday;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public void setDoctor(URI doctor) {
        this.doctor = doctor;
    }
}