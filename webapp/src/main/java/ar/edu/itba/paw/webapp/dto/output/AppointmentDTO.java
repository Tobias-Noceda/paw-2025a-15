package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class AppointmentDTO {
    
    private AppointmentStatusEnum status;
    private String weekday;
    private String address;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int duration; // Duration in minutes
    private String detail;

    private String patientName;
    private String patientEmail;

    private LinkDTO links;
    
    public static Function<AppointmentNew, AppointmentDTO> mapper(final AppointmentStatusEnum status, final UriInfo uriInfo) {
        return d -> fromAppointment(d, status, uriInfo);
    }

    public static AppointmentDTO fromAppointment(final AppointmentNew appointment, final AppointmentStatusEnum status, final UriInfo uriInfo) {
        final AppointmentDTO dto = new AppointmentDTO();

        DoctorSingleShift shift = appointment.getShift();
        
        dto.status = status;
        dto.weekday = shift.getWeekday().toString();
        dto.address = shift.getAddress();
        dto.date = appointment.getDate();
        dto.startTime = appointment.getId().getStartTime();
        dto.endTime = appointment.getId().getEndTime();
        dto.duration = shift.getDuration();
        dto.detail = appointment.getDetail();

        dto.patientName = appointment.getPatient() != null ? appointment.getPatient().getName() : null;
        dto.patientEmail = appointment.getPatient() != null ? appointment.getPatient().getEmail() : null;

        LinkDTO links = new LinkDTO();

        URI doctor = uriInfo.getBaseUriBuilder().path("doctors").path(String.valueOf(shift.getDoctor().getId())).build();
        URI self = uriInfo.getBaseUriBuilder().path("appointments").path(appointment.getId().toIdString()).build();
        
        links.setDoctor(doctor);
        links.setSelf(self);

        if (appointment.getPatient() != null) {
            URI patient = uriInfo.getBaseUriBuilder().path("patients").path(String.valueOf(appointment.getPatient().getId())).build();

            links.setPatient(patient);
        }

        dto.links = links;

        return dto;
    }

    // getters
    public AppointmentStatusEnum getStatus() {
        return status;
    }

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

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public LinkDTO getLinks() {
        return links;
    }

    // setters
    public void setStatus(AppointmentStatusEnum status) {
        this.status = status;
    }

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

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public void setLinks(LinkDTO links) {
        this.links = links;
    }
}
