package ar.edu.itba.paw.form;

import javax.validation.constraints.*;

import ar.edu.itba.paw.annotation.ValidArgPhone;
import ar.edu.itba.paw.form.constraints.PastDate;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.models.enums.BloodTypeEnum;

import java.time.LocalDate;
import java.util.List;

public class ProfileForm {
    
    MultipartFile profileImage;

    @ValidArgPhone(message = "{form.phoneNumber.invalid}")
    private String phoneNumber;

    @Positive
    @Max(value = 140, message = "{form.age.invalid}")
    private Integer age;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    private BloodTypeEnum bloodType;

    @Positive
    private Double height;

    @Positive
    private Double weight;

    private Boolean smokes;

    private Boolean drinks;

    @Size(max = 250)
    private String meds;

    @Size(max = 250)
    private String conditions;

    @Size(max = 250)
    private String allergies;

    @Size(max = 100)
    private String diet;

    @Size(max = 100)
    private String hobbies;

    @Size(max = 50)
    private String job;

    @NotNull(message = "{form.insurances.notNull}")
    private List<Long> insurances;

    private LocaleEnum mailLanguage;

    public LocaleEnum getMailLanguage() {
        return mailLanguage;
    }

    public void setMailLanguage(LocaleEnum mailLanguage) {
        this.mailLanguage = mailLanguage;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public MultipartFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAge(Integer age){
        this.age = age;
    }

    public Integer getAge(){
        return age;
    }

    public void setBloodType(BloodTypeEnum bloodType){
        this.bloodType = bloodType;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public void setHeight(Double height){
        this.height = height;
    }

    public Double getHeight() {
        return height;
    }

    public void setWeight(Double weight){
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    public void setSmokes(Boolean smokes){
        this.smokes = smokes;
    }

    public Boolean getSmokes() {
        return smokes;
    }

    public void setDrinks(Boolean drinks){
        this.drinks = drinks;
    }

    public Boolean getDrinks() {
        return drinks;
    }

    public void setMeds(String meds){
        this.meds = meds;
    }

    public String getMeds() {
        return meds;
    }

    public void setConditions(String conditions){
        this.conditions = conditions;
    }

    public String getConditions() {
        return conditions;
    }

    public void setAllergies(String allergies){
        this.allergies = allergies;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setDiet(String diet){
        this.diet = diet;
    }

    public String getDiet() {
        return diet;
    }

    public void setHobbies(String hobbies){
        this.hobbies = hobbies;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setJob(String job){
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public List<Long> getInsurances() { return insurances; }

    public void setInsurances(List<Long> insurances) { this.insurances = insurances; }
}
