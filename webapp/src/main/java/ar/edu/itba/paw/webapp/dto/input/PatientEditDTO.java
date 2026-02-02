package ar.edu.itba.paw.webapp.dto.input;

import java.time.LocalDate;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;
import ar.edu.itba.paw.webapp.form.constraints.PastDate;
import ar.edu.itba.paw.webapp.form.constraints.ValidArgPhone;

@NonEmptyBody
public class PatientEditDTO {

    @ValidArgPhone
    private String telephone;

    @Positive
    private Long pictureId;

    private String mailLanguage;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    private BloodTypeEnum bloodtype;

    @Range(min = 0, max = 3)
    private Double height;

    @Range(min = 0, max = 300)
    private Double weight;

    private Long insuranceId;

    @Pattern(regexp = "[0-9]*")
    private String insuranceNumber;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getPictureId() {
        return pictureId;
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }

    public String getMailLanguage() {
        return mailLanguage;
    }

    public void setMailLanguage(String mailLanguage) {
        this.mailLanguage = mailLanguage;
    }

    public LocalDate getBirthDate() { 
        return birthDate; 
    }

    public void setBirthDate(LocalDate birthDate) { 
        this.birthDate = birthDate; 
    }

    public BloodTypeEnum getBloodtype() {
        return  bloodtype;
    }

    public void setBloodType(BloodTypeEnum bloodtype) {
        this.bloodtype = bloodtype;
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

    public Long getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Long insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
    
}
