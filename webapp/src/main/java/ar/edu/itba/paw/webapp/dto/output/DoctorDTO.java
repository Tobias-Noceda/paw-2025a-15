package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Doctor;

public class DoctorDTO {
    private String email;
    private String name;
    private String telephone;
    private String license;
    private String specialty;

    private LinkDTO links;

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

        URI self = uriInfo.getBaseUriBuilder().path("doctors").path(String.valueOf(doctor.getId())).build();
        URI image = uriInfo.getBaseUriBuilder().path("images").path(String.valueOf(doctor.getPicture().getId())).build();
        URI schedule = uriInfo.getBaseUriBuilder().path("doctors").path(String.valueOf(doctor.getId())).path("shifts").build();
        URI insurances = uriInfo.getBaseUriBuilder().path("insurances").queryParam("supportedBy", String.valueOf(doctor.getId())).build();

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setImage(image)
            .setSchedule(schedule)
            .setInsurances(insurances)
        );

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

    public void setLinks(LinkDTO links){
        this.links = links;
    }

    @Override
    public String toString() {
        return "DoctorDTO [email=" + email + ", name=" + name + ", telephone=" + telephone + ", licence=" + license
                + ", specialty="
                + specialty + ", links=" + links + "]";
    }
}
