package ar.edu.itba.paw.models;

public class PatientDetail {
    private final long patientId;
    private final Integer age;
    private final BloodTypeEnum bloodType;
    private final Double height;
    private final Double weight;
    private final Boolean smokes;
    private final Boolean drinks;
    private final String meds;
    private final String conditions;
    private final String allergies;
    private final String diet;
    private final String hobbies;
    private final String job;

    public PatientDetail(long patientId, Integer age, BloodTypeEnum bloodType, Double height, Double weight, 
    Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet, String hobbies, String job) {
        this.patientId = patientId;
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

    public long getPatientId() {
        return patientId;
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
