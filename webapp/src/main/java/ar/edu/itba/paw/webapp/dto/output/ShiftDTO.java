package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.webapp.controller.DoctorController;

public class ShiftDTO {
    private WeekdayEnum weekday;
    private String address;
    private String startTime;
    private String endTime;
    private int duration;

    private LinkDTO links;

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

        URI baseSelf = uriInfo.getBaseUriBuilder().path("shifts").path(String.valueOf(shift.getId())).build();
        TemplatedLinkDTO self = TemplatedLinkDTO.of(baseSelf);
        URI baseDoctor = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(shift.getDoctor().getId())).build();
        TemplatedLinkDTO doctor = TemplatedLinkDTO.of(baseDoctor);

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setDoctor(doctor)
        );
        
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

    public LinkDTO getLinks(){
        return links;
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

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}