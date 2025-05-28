package ar.edu.itba.paw.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@Entity
@Table(name = "patient_details")
@PrimaryKeyJoinColumn(name = "patient_id")
@DiscriminatorValue("0")
public class Patient extends User {
    
    @Column(name = "patient_birthdate")
    private LocalDate birthdate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "patient_blood_type")
    private BloodTypeEnum bloodType;

    @Column(name = "patient_height")
    private BigDecimal height;

    @Column(name = "patient_weight")
    private BigDecimal weight;

    @Column(name = "patient_smokes")
    private Boolean smokes;

    @Column(name = "patient_drinks")
    private Boolean drinks;

    @Column(name = "patient_meds", length = 250)
    private String meds;

    @Column(name = "patient_conditions", length = 250)
    private String conditions;

    @Column(name = "patient_allergies", length = 250)
    private String allergies;

    @Column(name = "patient_diet", length = 100)
    private String diet;

    @Column(name = "patient_hobbies", length = 100)
    private String hobbies;

    @Column(name = "patient_job", length = 50)
    private String job;

    public Patient() {
        super();
    }

    public Patient(String email, String password, String name, String telephone, File picture, LocalDate createDate, LocaleEnum locale,
                   LocalDate birthdate, BigDecimal height, BigDecimal weight) {
        super(email, password, name, telephone, UserRoleEnum.PATIENT, picture, createDate, locale);
        this.birthdate = birthdate;
        this.height = height;
        this.weight = weight;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Boolean getSmokes() {
        return smokes;
    }

    public void setSmokes(Boolean smokes) {
        this.smokes = smokes;
    }

    public Boolean getDrinks() {
        return drinks;
    }

    public void setDrinks(Boolean drinks) {
        this.drinks = drinks;
    }

    public String getMeds() {
        return meds;
    }

    public void setMeds(String meds) {
        this.meds = meds;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
