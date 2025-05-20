package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.Period;

import ar.edu.itba.paw.models.enums.BloodTypeEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "patient_details")
public class PatientDetail {
    @Id
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private User patient;

    @Column( name = "patient_birthdate")
    private LocalDate birthdate;

    @Enumerated
    @Column( name = "patient_blood_type")
    private BloodTypeEnum bloodType;

    @Column( name = "patient_height")
    private Double height;

    @Column( name = "patient_weight")
    private Double weight;

    @Column( name = "patient_smokes")
    private Boolean smokes;

    @Column( name = "patient_drinks")
    private Boolean drinks;

    @Column( name = "patient_meds", length = 250)
    private String meds;

    @Column( name = "patient_conditions", length = 250)
    private String conditions;

    @Column( name = "patient_allergies", length = 250)
    private String allergies;

    @Column( name = "patient_diet", length = 100)
    private String diet;

    @Column( name = "patient_hobbies", length = 100)
    private String hobbies;

    @Column( name = "patient_job", length = 50)
    private String job;

    public PatientDetail(){
        //just for hibernate;
    }

    public PatientDetail(User patient, LocalDate birthdate, BloodTypeEnum bloodType, Double height, Double weight, 
    Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job) {
        this.patient = patient;
        this.birthdate = birthdate;
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

    public User getPatient() {
        return patient;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate){
        this.birthdate = birthdate;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodTypeEnum bloodType){
        this.bloodType = bloodType;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height){
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight){
        this.weight = weight;
    }

    public Boolean getSmokes() {
        return smokes;
    }

    public void setSmokes(Boolean smokes){
        this.smokes = smokes;
    }

    public Boolean getDrinks() {
        return drinks;
    }

    public void setDrinks(Boolean drinks){
        this.drinks = drinks;
    }

    public String getMeds() {
        return meds;
    }

    public void setMeds(String meds){
        this.meds = meds;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions){
        this.conditions = conditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies){
        this.allergies = allergies;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet){
        this.diet = diet;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies){
        this.hobbies = hobbies;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job){
        this.job = job;
    }

    public Integer getAge(){
        if(birthdate==null) return null;
        return Period.between(this.birthdate, LocalDate.now()).getYears();
    }
    
    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof PatientDetail)) return false;

        PatientDetail o = (PatientDetail) other;

        return (this.patient==null?(o.patient==null):(this.patient.equals(o.patient))) 
        && (this.birthdate==null?(o.birthdate==null):(this.birthdate.equals(o.birthdate)))
        && (this.bloodType==null?(o.bloodType==null):(this.bloodType.equals(o.bloodType)))
        && (this.height==null?(o.height==null):(this.height.equals(o.height)))
        && (this.weight==null?(o.weight==null):(this.weight.equals(o.weight)))
        && (this.smokes==null?(o.smokes==null):(this.smokes.equals(o.smokes)))
        && (this.drinks==null?(o.drinks==null):(this.drinks.equals(o.drinks)))
        && (this.meds==null?(o.meds==null):(this.meds.equals(o.meds)))
        && (this.conditions==null?(o.conditions==null):(this.conditions.equals(o.conditions)))
        && (this.allergies==null?(o.allergies==null):(this.allergies.equals(o.allergies)))
        && (this.diet==null?(o.diet==null):(this.diet.equals(o.diet)))
        && (this.hobbies==null?(o.hobbies==null):(this.hobbies.equals(o.hobbies)))
        && (this.job==null?(o.job==null):(this.job.equals(o.job)));
    }

    @Override
    public int hashCode() {
        int result = patient.hashCode();
        result = 31 * result + birthdate.hashCode();
        result = 31 * result + bloodType.hashCode();
        result = 31 * result + Double.hashCode(height);
        result = 31 * result + Double.hashCode(weight);
        result = 31 * result + Boolean.hashCode(smokes);
        result = 31 * result + Boolean.hashCode(drinks);
        result = 31 * result + meds.hashCode();
        result = 31 * result + conditions.hashCode();
        result = 31 * result + allergies.hashCode();
        result = 31 * result + diet.hashCode();
        result = 31 * result + hobbies.hashCode();
        result = 31 * result + job.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "PatientDetails{" +
            "patient=" + patient +
            ", birthdate=" + birthdate +
            ", bloodType=" + bloodType +
            ", height=" + height +
            ", weight=" + weight +
            ", smokes=" + smokes +
            ", drinks=" + drinks +
            ", meds=" + meds +
            ", conditions=" + conditions +
            ", allergies=" + allergies +
            ", diet=" + diet +
            ", hobbies=" + hobbies +
            ", job=" + job +
            '}';
    }
}
