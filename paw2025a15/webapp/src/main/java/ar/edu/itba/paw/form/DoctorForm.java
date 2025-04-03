package ar.edu.itba.paw.form;

import ar.edu.itba.paw.models.Schedule;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class DoctorForm {
    @Size(min = 6, max = 100)
    @Pattern(regexp = "[a-zA-Z]+")
    private String name;
    @Size(min = 6, max = 100)
    @Pattern(regexp = "[a-zA-Z]+")
    private String surname;
    private String speciality;
    private List<String> ObrasSociales;
    private Schedule schedules;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Schedule getSchedules() {
        return schedules;
    }

    public void setSchedules(Schedule schedules) {
        this.schedules = schedules;
    }

    public String getSpeciality() {
        return speciality;
    }

    public List<String> getObrasSociales() {
        return ObrasSociales;
    }

    public void setObrasSociales(List<String> obrasSociales) {
        ObrasSociales = obrasSociales;
    }

    @Size(min = 6, max = 100)
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialty() {
        return speciality;
    }
}
