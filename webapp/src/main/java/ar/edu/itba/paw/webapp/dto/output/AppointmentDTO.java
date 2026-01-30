package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class AppointmentDTO {
    
    private String weekday;
    private String address;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int duration; // Duration in minutes
    private String detail;

    private URI doctor;
    private URI patient;
    private URI self;
    
    public static Function<AppointmentNew, AppointmentDTO> mapper(final UriInfo uriInfo) {
        return d -> fromAppointment(d, uriInfo);
    }

    public static AppointmentDTO fromAppointment(final AppointmentNew appointment, final UriInfo uriInfo) {
        final AppointmentDTO dto = new AppointmentDTO();

        DoctorSingleShift shift = appointment.getShift();
        
        dto.weekday = shift.getWeekday().toString();
        dto.address = shift.getAddress();
        dto.date = appointment.getDate();
        dto.startTime = appointment.getId().getStartTime();
        dto.endTime = appointment.getId().getEndTime();
        dto.duration = shift.getDuration();
        dto.detail = appointment.getDetail();

        dto.doctor = uriInfo.getBaseUriBuilder().path("doctors").path(String.valueOf(shift.getDoctor().getId())).build();

        if (appointment.getPatient() != null) {
            dto.patient = uriInfo.getBaseUriBuilder().path("patients").path(String.valueOf(appointment.getPatient().getId())).build();
            dto.self = uriInfo.getBaseUriBuilder().path("appointments").path(appointment.getId().toIdString()).build();
        }

        return dto;
    }

    // getters
    public String getWeekday() {
        return weekday;
    }

    public String getAddress() {
        return address;
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

    public int getDuration() {
        return duration;
    }

    public String getDetail() {
        return detail;
    }

    public URI getDoctor() {
        return doctor;
    }

    public URI getPatient() {
        return patient;
    }

    public URI getSelf() {
        return self;
    }

    // setters
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public void setWeekday(WeekdayEnum weekday) {
        this.weekday = weekday.toString();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setDoctor(URI doctor) {
        this.doctor = doctor;
    }

    public void setPatient(URI patient) {
        this.patient = patient;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
