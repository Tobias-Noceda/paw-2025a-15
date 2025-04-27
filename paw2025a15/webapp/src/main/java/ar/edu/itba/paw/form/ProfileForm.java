package ar.edu.itba.paw.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.models.BloodTypeEnum;

public class ProfileForm {//TODO constraints en Strings por el largo del input, prob tmb una que prevenga sql injection
    
    MultipartFile profileImage;

    @Size(min = 5, max = 15, message = "Phone number must be between 5 and 15 characters")
    String phoneNumber;

    @Min(value = 0, message = "Age cannot be less than 0")
    @Max(value = 120, message = "Age cannot be greater than 120")
    private Integer age;

    private BloodTypeEnum bloodType;

    @Positive(message = "Height must be positive")
    private Double height;

    @Positive(message = "Weight must be positive")
    private Double weight;

    private Boolean smokes;

    private Boolean drinks;

    @Size(max = 250, message = "Medications field cannot exceed 250 characters")
    private String meds;

    @Size(max = 250, message = "Medications field cannot exceed 250 characters")
    private String conditions;

    @Size(max = 250, message = "Medications field cannot exceed 250 characters")
    private String allergies;

    @Size(max = 100, message = "Medications field cannot exceed 100 characters")
    private String diet;

    @Size(max = 100, message = "Medications field cannot exceed 100 characters")
    private String hobbies;

    @Size(max = 50, message = "Medications field cannot exceed 50 characters")
    private String job;

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

}
