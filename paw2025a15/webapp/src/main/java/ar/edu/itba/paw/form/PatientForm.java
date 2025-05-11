package ar.edu.itba.paw.form;

import ar.edu.itba.paw.form.constraints.PastDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class PatientForm {
    @NotEmpty(message = "{form.name.notEmpty}")
    private String name;

    @NotEmpty(message = "{form.surname.notEmpty}")
    private String surname;

    @Size(min=8, message="{form.password.size}")
    private String password;

    private String confirmPassword;

    @NotEmpty(message = "{form.email.notEmpty}")
    @Email(message = "{form.email.invalid}")
    private String email;

    @NotEmpty(message = "{form.phoneNumber.notEmpty}")
    @Size(min=8, max=15, message = "{form.phoneNumber.invalid}")
    private String phoneNumber;

    @Positive
    private Double height;

    @Positive
    private Double weight;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    public Double getHeight() { return height; }

    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }

    public void setWeight(Double weight) { this.weight = weight; }

    public LocalDate getBirthDate() { return birthDate; }

    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}