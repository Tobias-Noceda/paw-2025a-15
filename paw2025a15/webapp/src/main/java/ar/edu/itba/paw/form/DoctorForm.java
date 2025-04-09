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
    @Size(min = 1, max = 50, message = "name must be less than 50 characters")
    @NotEmpty(message = "{doctorForm.name.notEmpty}")
    private String name;

    @Size(min = 1, max = 50, message = "surname must be less than 50 characters")
    @NotEmpty(message = "{doctorForm.surname.notEmpty}")
    private String surname;

    @NotNull(message = "{doctorForm.specialty.notNull}")
    private SpecialtyEnum speciality;

    @NotNull(message = "{doctorForm.obrasSociales.notNull}")
    //@NotEmpty(message = "{doctorForm.obrasSociales.notEmpty}")
    private List<Long> ObrasSociales;

    @NotNull(message = "{doctorForm.schedules.notNull}")
    private Schedule schedules;

    @Size(min = 1, max = 50, message = "address must be less than 50 characters")
    @NotEmpty(message = "{doctorForm.address.notEmpty}")
    private String address;

    @NotEmpty(message = "{doctorForm.email.notEmpty}")
    @Email(message = "{doctorForm.email.invalid}")
    private String email;

    @NotNull(message = "{doctorForm.amount.notNull}")
    @Positive(message = "{doctorForm.amount.positive}")
    private int amount;



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
