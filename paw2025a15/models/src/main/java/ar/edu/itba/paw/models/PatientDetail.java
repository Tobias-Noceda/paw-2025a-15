package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.Period;

import ar.edu.itba.paw.models.enums.BloodTypeEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "patient_details")
public class PatientDetail {
    @Column( name = "patient_id")
    private final long patientId;
    @Column( name = "patient_birthdate")
    private final LocalDate birthdate;
    @Column( name = "patient_blood_type")
    private final BloodTypeEnum bloodType;
    @Column( name = "patient_height")
    private final Double height;
    @Column( name = "patient_weight")
    private final Double weight;
    @Column( name = "patient_smokes")
    private final Boolean smokes;
    @Column( name = "patient_drinks")
    private final Boolean drinks;
    @Column( name = "patient_meds", length = 250)
    private final String meds;
    @Column( name = "patient_conditions", length = 250)
    private final String conditions;
    @Column( name = "patient_allergies", length = 250)
    private final String allergies;
    @Column( name = "patient_diet", length = 100)
    private final String diet;
    @Column( name = "patient_hobbies", length = 100)
    private final String hobbies;
    @Column( name = "patient_job", length = 50)
    private final String job;

    public PatientDetail(long patientId, LocalDate birthdate, BloodTypeEnum bloodType, Double height, Double weight, 
    Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job) {
        this.patientId = patientId;
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

    public long getPatientId() {
        return patientId;
        }

    public LocalDate getBirthdate() {
        return birthdate;
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

    public String   getMeds() {
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

    public Integer getAge(){
        if(birthdate==null) return null;
        return Period.between(this.birthdate, LocalDate.now()).getYears();
    }
    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof PatientDetail)) return false;

        PatientDetail o = (PatientDetail) other;

        return (this.patientId==o.patientId) 
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
        int result = Long.hashCode(patientId);
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
            "patientId=" + patientId +
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
