package ar.edu.itba.paw.webapp.dto;

import java.net.URI;
import java.time.LocalDate;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Doctor;

public class DoctorDTO {
    private String email;
    private String name;
    private String telephone;
    private String license;
    private String specialty;

    private URI self;
    private URI image;
    private URI schedule;
    private URI todaysFreeAppointments;
    private URI insurances;

    public static Function<Doctor, DoctorDTO> mapper(final UriInfo uriInfo) {
        return d -> fromDoctor(d,uriInfo);
    }

    public static DoctorDTO fromDoctor(final Doctor doctor, final UriInfo uriInfo) {
        final DoctorDTO dto = new DoctorDTO();

        dto.email = doctor.getEmail();
        dto.name = doctor.getName();
        dto.telephone = doctor.getTelephone();
        dto.license = doctor.getLicence();
        dto.specialty = doctor.getSpecialty().toString();

        dto.self = uriInfo.getBaseUriBuilder().path("doctors").path(String.valueOf(doctor.getId())).build();
        dto.image = uriInfo.getBaseUriBuilder().path("files").path(String.valueOf(doctor.getPicture().getId())).build();
        dto.schedule = uriInfo.getBaseUriBuilder().path("doctors").path(String.valueOf(doctor.getId())).path("shifts").build();
        dto.todaysFreeAppointments = uriInfo.getBaseUriBuilder().path("appointments").queryParam("doctorId", String.valueOf(doctor.getId())).queryParam("date", LocalDate.now()).build();
        dto.insurances = uriInfo.getBaseUriBuilder().path("insurances").queryParam("supportedBy", String.valueOf(doctor.getId())).build();

        return dto;
    }

    // getters
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getLicense() {
        return license;
    }

    public String getSpecialty() {
        return specialty;
    }

    public URI getSelf() {
        return self;
    }

    public URI getImage() {
        return image;
    }

    public URI getSchedule() {
        return schedule;
    }

    public URI getTodaysFreeAppointments() {
        return todaysFreeAppointments;
    }

    public URI getInsurances() {
        return insurances;
    }

    // setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public void setSchedule(URI schedule) {
        this.schedule = schedule;
    }

    public void setTodaysFreeAppointments(URI todaysFreeAppointments) {
        this.todaysFreeAppointments = todaysFreeAppointments;
    }

    public void setInsurances(URI insurances) {
        this.insurances = insurances;
    }

    @Override
    public String toString() {
        return "DoctorDTO [email=" + email + ", name=" + name + ", telephone=" + telephone + ", licence=" + license
                + ", specialty="
                + specialty + ", self=" + self + ", image=" + image + ", schedule=" + schedule + "]";
    }
}
