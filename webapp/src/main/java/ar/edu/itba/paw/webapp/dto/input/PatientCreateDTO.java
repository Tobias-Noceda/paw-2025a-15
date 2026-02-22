package ar.edu.itba.paw.webapp.dto.input;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import ar.edu.itba.paw.webapp.form.constraints.PastDate;
import ar.edu.itba.paw.webapp.form.constraints.ValidArgPhone;

public class PatientCreateDTO {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 8)
    private String password;

    @NotNull
    @ValidArgPhone
    private String telephone;

    @Range(min = 0, max = 3)
    private Double height;

    @Range(min = 0, max = 300)
    private Double weight;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthdate;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Double getHeight() { 
        return height;
    }

    public void setHeight(Double height) { 
        this.height = height;
    }

    public Double getWeight() { 
        return weight; 
    }

    public void setWeight(Double weight) { 
        this.weight = weight; 
    }

    public LocalDate getBirthdate() { 
        return birthdate; 
    }

    public void setBirthdate(LocalDate birthdate) { 
        this.birthdate = birthdate; 
    }
    
    @Override
    public String toString() {
        return "PatientCreateDTO [name=" + name + ", email=" + email + ", password=" + password + ", telephone=" + telephone
                + ", height=" + height + ", weight=" + weight + ", birthdate=" + birthdate + "]";
    }
}
