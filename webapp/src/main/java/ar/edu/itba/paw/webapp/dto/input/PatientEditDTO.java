package ar.edu.itba.paw.webapp.dto.input;

import java.time.LocalDate;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;
import ar.edu.itba.paw.webapp.form.constraints.PastDate;
import ar.edu.itba.paw.webapp.form.constraints.ValidArgPhone;

@NonEmptyBody
public class PatientEditDTO {

    @ValidArgPhone
    private String telephone;

    @URL(protocol = "http")
    private String picture;

    @Pattern(regexp = "ES_AR|ES_US|EN_AR|EN_US")
    private String mailLanguage;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthdate;

    @Pattern(regexp = "^(O\\+|O-|A\\+|A-|B\\+|B-|AB\\+|AB-)$")
    private String bloodType;

    @Range(min = 0, max = 3)
    private Double height;

    @Range(min = 0, max = 300)
    private Double weight;
    
    @URL(protocol = "http")
    private String insurance;

    @Pattern(regexp = "[0-9]*")
    private String insuranceNumber;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMailLanguage() {
        return mailLanguage;
    }

    public void setMailLanguage(String mailLanguage) {
        this.mailLanguage = mailLanguage;
    }

    public LocalDate getBirthdate() { 
        return birthdate; 
    }

    public void setBirthdate(LocalDate birthdate) { 
        this.birthdate = birthdate; 
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
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

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
    
}
