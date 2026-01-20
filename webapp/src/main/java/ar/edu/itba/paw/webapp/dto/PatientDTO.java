package ar.edu.itba.paw.webapp.dto;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Patient;

public class PatientDTO {
    
    private String email;
    private String name;
    private String telephone;

    private LocalDate birthDate;
    private String bloodType;
    private BigDecimal height;
    private BigDecimal weight;

    private Boolean smokes;
    private Boolean drinks;

    private String meds;
    private String conditions;
    private String allergies;
    private String diet;
    private String hobbies;
    private String job;
    private String insuranceName;
    private String insuranceNumber;

    private URI self;

    public static Function<Patient, PatientDTO> mapper(final UriInfo uriInfo) {
        return p -> fromPatient(p, uriInfo);
    }

    public static PatientDTO fromPatient(final Patient patient, final UriInfo uriInfo) {
        final PatientDTO dto = new PatientDTO();

        dto.email = patient.getEmail();
        dto.name = patient.getName();
        dto.telephone = patient.getTelephone();
        dto.birthDate = patient.getBirthdate();
        dto.bloodType = patient.getBloodType().toString();
        dto.height = patient.getHeight();
        dto.weight = patient.getWeight();
        dto.smokes = patient.getSmokes();
        dto.drinks = patient.getDrinks();
        dto.meds = patient.getMeds();
        dto.conditions = patient.getConditions();
        dto.allergies = patient.getAllergies();
        dto.diet = patient.getDiet();
        dto.hobbies = patient.getHobbies();
        dto.job = patient.getJob();
        dto.insuranceName = patient.getInsurance().getName();
        dto.insuranceNumber = patient.getInsuranceNumber();

        dto.self = uriInfo.getBaseUriBuilder().path("patients").path(String.valueOf(patient.getId())).build();

        return dto;
    }

    // getters
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getBloodType() {
        return bloodType;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public BigDecimal getWeight() {
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

    public String getInsuranceName() {
        return insuranceName;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public URI getSelf() {
        return self;
    }

    // setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setSmokes(Boolean smokes) {
        this.smokes = smokes;
    }

    public void setDrinks(Boolean drinks) {
        this.drinks = drinks;
    }

    public void setMeds(String meds) {
        this.meds = meds;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
