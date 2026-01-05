package ar.edu.itba.paw.webapp.dto;

import java.net.URL;

import ar.edu.itba.paw.models.entities.Doctor;

public class DoctorDTO {
    private String email;
    private String name;
    private String telephone;
    private String licence;
    private String specialty;

    private URL self;
    private URL image;
    private URL schedule;

    public static DoctorDTO fromDoctor(final Doctor doctor) {
        final DoctorDTO dto = new DoctorDTO();

        dto.email = doctor.getEmail();
        dto.name = doctor.getName();
        dto.telephone = doctor.getTelephone();
        dto.licence = doctor.getLicence();
        dto.specialty = doctor.getSpecialty().toString();

        // TODO: set URLs

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

    public String getLicence() {
        return licence;
    }

    public String getSpecialty() {
        return specialty;
    }

    public URL getSelf() {
        return self;
    }

    public URL getImage() {
        return image;
    }

    public URL getSchedule() {
        return schedule;
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

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setSelf(URL self) {
        this.self = self;
    }

    public void setImage(URL image) {
        this.image = image;
    }

    public void setSchedule(URL schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "DoctorDTO [email=" + email + ", name=" + name + ", telephone=" + telephone + ", licence=" + licence + ", specialty="
                + specialty + ", self=" + self + ", image=" + image + ", schedule=" + schedule + "]";
    }
}
