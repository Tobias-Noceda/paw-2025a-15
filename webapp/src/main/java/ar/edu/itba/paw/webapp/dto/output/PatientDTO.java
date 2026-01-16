package ar.edu.itba.paw.webapp.dto.output;

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
    private LocalDate birthdate;
    private String bloodtype;
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
    private String insuranceNumber;

    private URI self;
    private URI picture;
    private URI insurance;
    private URI doctors;

    public static Function<Patient, PatientDTO> mapper(final UriInfo uriInfo){
        return (p) -> fromPatient(uriInfo, p);
    }

    public static PatientDTO fromPatient(final UriInfo uriInfo, Patient patient){
        final PatientDTO dto = new PatientDTO();

        dto.name = patient.getName();
        dto.email = patient.getEmail();
        dto.telephone = patient.getTelephone();
        dto.birthdate = patient.getBirthdate();
        if(patient.getBloodType()!=null) dto.bloodtype = patient.getBloodType().getName();
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
        dto.insuranceNumber = patient.getInsuranceNumber();

        dto.self = uriInfo.getBaseUriBuilder().path("patients").path(String.valueOf(patient.getId())).build();
        dto.picture = uriInfo.getBaseUriBuilder().path("files").path(String.valueOf(patient.getPicture().getId())).build();
        if(patient.getInsurance()!=null) dto.insurance = uriInfo.getBaseUriBuilder().path("insurances").path(String.valueOf(patient.getInsurance().getId())).build();
        //dto.doctors = uriInfo.getBaseUriBuilder().path("doctors").path(String.valueOf(patient.getPicture().getId())).build(); TODO filtered GET in doctors controller

        return dto;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getTelephone(){
        return telephone;
    }

    public void setTelephone(String telephone){
        this.telephone = telephone;
    }

    public LocalDate getBirthDate(){
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate){
        this.birthdate = birthdate;
    }

    public String getBloodtype(){
        return bloodtype;
    }

    public void setBloodtype(String bloodtype){
        this.bloodtype = bloodtype;
    }

    public BigDecimal getHeight(){
        return height;
    }

    public void setHeight(BigDecimal height){
        this.height = height;
    }

    public BigDecimal getWeight(){
        return weight;
    }

    public void setWeight(BigDecimal weight){
        this.weight = weight;
    }

    public Boolean getSmokes(){
        return smokes;
    }

    public void setSmokes(Boolean smokes){
        this.smokes = smokes;
    }

    public Boolean getDrinks(){
        return drinks;
    }

    public void setDrinks(Boolean drinks){
        this.drinks = drinks;
    }

    public String getMeds(){
        return meds;
    }

    public void setMeds(String meds){
        this.meds = meds;
    }

    public String getConditions(){
        return conditions;
    }

    public void setConditions(String conditions){
        this.conditions = conditions;
    }

    public String getAllergies(){
        return allergies;
    }

    public void setAllergies(String allergies){
        this.allergies = allergies;
    }

    public String getDiet(){
        return diet;
    }

    public void setDiet(String diet){
        this.diet = diet;
    }

    public String getHobbies(){
        return hobbies;
    }

    public void setHobbies(String hobbies){
        this.hobbies = hobbies;
    }

    public String getJob(){
        return job;
    }

    public void setJob(String job){
        this.job = job;
    }

    public String getInsuranceNumber(){
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber){
        this.insuranceNumber = insuranceNumber;
    }
 
    public URI getSelf(){
        return self;
    }

    public void setSelf(URI self){
        this.self = self;
    }

    public URI getPicture(){
        return picture;
    }

    public void setPicture(URI picture){
        this.picture = picture;
    }

    public URI getInsurance(){
        return insurance;
    }

    public void setInsurance(URI insurance){
        this.insurance = insurance;
    }

    public URI getDoctors(){
        return doctors;
    }

    public void setDoctors(URI doctors){
        this.doctors = doctors;
    }
}
