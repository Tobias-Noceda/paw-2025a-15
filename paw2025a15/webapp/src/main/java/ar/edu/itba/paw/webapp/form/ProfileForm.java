package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.models.Schedule;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.webapp.form.constraints.PastDate;
import ar.edu.itba.paw.webapp.form.constraints.ValidArgPhone;
import ar.edu.itba.paw.webapp.form.constraints.ValidInsuranceNumber;
import ar.edu.itba.paw.webapp.form.constraints.ValidProfileImage;
import ar.edu.itba.paw.webapp.form.constraints.ValidSchedule;

@ValidInsuranceNumber
public class ProfileForm {

    public ProfileForm() {
        this.insuranceId = null;
        this.insuranceNumber = "";
        this.insurances = List.of();
        this.updateSchedules = false;
        this.schedules = new Schedule();
        this.address = "";
        this.amount = 0;
        this.keepTurns = false;
    }

    // general
    @ValidProfileImage
    MultipartFile profileImage;

    @ValidArgPhone(message = "{form.phoneNumber.invalid}")
    private String phoneNumber;

    private LocaleEnum mailLanguage;

    // Patient specific
    @Positive
    @Max(value = 140, message = "{form.age.invalid}")
    private Integer age;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    private BloodTypeEnum bloodType;

    @Range(min = 0, max = 3, message = "{form.height.invalid}")
    private Double height;

    @Range(min = 0, max = 300, message = "{form.weight.invalid}")
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

    private Long insuranceId;

    @Pattern(regexp = "[0-9]*", message = "{form.insurances.invalid}")
    private String insuranceNumber;

    // Doctor specific
    @NotNull(message = "{form.insurances.notNull}")
    private List<Long> insurances;

    private Boolean updateSchedules;

    @ValidSchedule
    private Schedule schedules;

    @NotEmpty(message = "{form.address.notEmpty}")
    private String address;

    private int amount;

    private boolean keepTurns;

    // Getters
    public MultipartFile getProfileImage() {
        return profileImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocaleEnum getMailLanguage() {
        return mailLanguage;
    }

    public Integer getAge(){
        return age;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public Double getHeight() {
        return height;
    }

    public Double getWeight() {
        return weight;
    }

    public Boolean getSmokes() {
        return smokes;
    }

    public Boolean getDrinks() {
        return drinks;
    }

    public String getMeds() {
        return meds;
    }

    public String getConditions() {
        return conditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getDiet() {
        return diet;
    }

    public String getHobbies() {
        return hobbies;
    }

    public String getJob() {
        return job;
    }

    public Long getInsuranceId() {
        return insuranceId;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public List<Long> getInsurances() {
        return insurances;
    }
    
    public Boolean getUpdateSchedules() {
        return updateSchedules;
    }

    public boolean getKeepTurns() {
        return keepTurns;
    }

    
    public Schedule getSchedules() {
        return schedules;
    }

    public String getAddress() {
        return address;
    }

    public int getAmount() {
        return amount;
    }

    // Setters
    public void setProfileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMailLanguage(LocaleEnum mailLanguage) {
        this.mailLanguage = mailLanguage;
    }

    public void setAge(Integer age){
        this.age = age;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setBloodType(BloodTypeEnum bloodType){
        this.bloodType = bloodType;
    }

    public void setHeight(Double height){
        this.height = height;
    }

    public void setWeight(Double weight){
        this.weight = weight;
    }

    public void setSmokes(Boolean smokes){
        this.smokes = smokes;
    }

    public void setDrinks(Boolean drinks){
        this.drinks = drinks;
    }

    public void setMeds(String meds){
        this.meds = meds;
    }

    public void setConditions(String conditions){
        this.conditions = conditions;
    }

    public void setAllergies(String allergies){
        this.allergies = allergies;
    }

    public void setDiet(String diet){
        this.diet = diet;
    }

    public void setHobbies(String hobbies){
        this.hobbies = hobbies;
    }

    public void setJob(String job){
        this.job = job;
    }

    public void setInsuranceId(Long insurance) {
        this.insuranceId = insurance;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public void setInsurances(List<Long> insurances) {
        this.insurances = insurances;
    }

    public void setUpdateSchedules(Boolean updateSchedules) {
        this.updateSchedules = updateSchedules;
    }
    
    public void setSchedules(Schedule schedules) {
        this.schedules = schedules;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setKeepTurns(boolean keepTurns) {
        this.keepTurns = keepTurns;
    }
}
