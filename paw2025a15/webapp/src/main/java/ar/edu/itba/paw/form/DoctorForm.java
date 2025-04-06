package ar.edu.itba.paw.form;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import ar.edu.itba.paw.models.Schedule;
import ar.edu.itba.paw.models.SpecialtyEnum;

public class DoctorForm {
    @NotNull
    @NotEmpty
    @Pattern(regexp = "[a-zA-Z]+")
    private String name;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "[a-zA-Z]+")
    private String surname;

    @NotNull
    private SpecialtyEnum speciality;

    @NotNull
    @NotEmpty
    private List<Long> ObrasSociales;

    @NotNull
    private Schedule schedules;

    @NotNull
    @NotEmpty
    private String address;

    @NotNull
    @NotEmpty
    private String email;

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

    public SpecialtyEnum getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SpecialtyEnum speciality) {
        this.speciality = speciality;
    }

    public List<Long> getObrasSociales() {
        return ObrasSociales;
    }

    public void setObrasSociales(List<Long> obrasSociales) {
        ObrasSociales = obrasSociales;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public SpecialtyEnum getSpecialty() {
        return speciality;
    }
}
