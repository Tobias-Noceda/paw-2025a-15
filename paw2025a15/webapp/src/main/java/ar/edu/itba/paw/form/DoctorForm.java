package ar.edu.itba.paw.form;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import ar.edu.itba.paw.models.Schedule;
import ar.edu.itba.paw.models.SpecialtyEnum;


public class DoctorForm {
    @Size(min = 1, max = 50, message = "{form.name.size}")
    @NotEmpty(message = "{form.name.notEmpty}")
    private String name;

    @Size(min = 1, max = 50, message = "{form.surname.size}")
    @NotEmpty(message = "{form.surname.notEmpty}")
    private String surname;

    @Size(min=8)
    @NotEmpty
    private String password;
    
    @NotNull(message = "{form.specialty.notNull}")
    private SpecialtyEnum speciality;

    @NotNull(message = "{form.insurances.notNull}")
    private List<Long> ObrasSociales;

    @NotNull(message = "{form.schedules.notNull}")
    private Schedule schedules;

    @Size(min = 1, max = 50, message = "{form.address.size}")
    @NotEmpty(message = "{form.address.notEmpty}")
    private String address;

    @Size(min = 1, max = 100, message = "{form.email.size}")
    @NotEmpty(message = "{dorm.email.notEmpty}")
    @Email(message = "{form.email.invalid}")
    private String email;

    @NotNull(message = "{doctorForm.amount.notNull}")
    @Positive(message = "{doctorForm.amount.positive}")
    private int amount;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
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

    public void setSurname(String surname) {
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

    public int getAmount(){
        return amount;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }
}
