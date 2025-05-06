package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.enums.BloodTypeEnum;

public class PatientView {//TODO: if not used, remove
    private final long id;
    private final String email;
    private final String name;
    private final String telephone;
    private final long pictureId;
    private Integer age = null;
    private BloodTypeEnum bloodType = null;
    private Double height = null;
    private Double weight = null;
    private Boolean smokes = null;
    private Boolean drinks = null;
    private String meds = null;
    private String conditions = null;
    private String allergies = null;
    private String diet = null;
    private String hobbies = null;
    private String job = null;

    public PatientView(long id, String email, String name, String telephone, long pictureId, Integer age, BloodTypeEnum bloodType, Double height, Double weight) {
        this.id = id;
        this. email = email;
        this.name= name;
        this.telephone = telephone;
        this.pictureId = pictureId;
        this.age = age;
        this.bloodType = bloodType;
        this.height = height;
        this.weight = weight;
    }

    public PatientView(long id, String email, String name, String telephone, long pictureId, Integer age, BloodTypeEnum bloodType, Double height, Double weight, 
    Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job) {
        this.id = id;
        this. email = email;
        this.name= name;
        this.telephone = telephone;
        this.pictureId = pictureId;
        this.age = age;
        this.bloodType = bloodType;
        this.height = height;
        this.weight = weight;
        this.smokes = smokes;
        this.drinks = drinks;
        this.meds = meds;
        this.conditions = conditions;
        this.allergies = allergies;
        this.diet = diet;
        this.hobbies = hobbies;
        this.job = job;
    }

    public void setViewBasic(Integer age, BloodTypeEnum bloodType, Double height, Double weight){
        this.age = age;
        this.bloodType = bloodType;
        this.height = height;
        this.weight = weight;
    }

    public void setViewMedical(String meds, String conditions, String allergies){
        this.meds = meds;
        this.conditions = conditions;
        this.allergies = allergies;
    }

    public void setViewHabits(Boolean smokes, Boolean drinks, String diet){
        this.smokes = smokes;
        this.drinks = drinks;
        this.diet = diet;
    }

    public void setViewSocial(String hobbies, String job){
        this.hobbies = hobbies;
        this.job = job;
    }

    public long getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }

    public String getName(){
        return name;
    }

    public String getTelephone(){
        return telephone;
    }

    public long getPictureId(){
        return pictureId;
    }

    public Integer getAge() {
        return age;
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

}
