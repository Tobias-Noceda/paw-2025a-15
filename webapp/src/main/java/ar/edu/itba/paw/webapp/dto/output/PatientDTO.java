package ar.edu.itba.paw.webapp.dto.output;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.webapp.controller.DoctorController;
import ar.edu.itba.paw.webapp.controller.FileController;
import ar.edu.itba.paw.webapp.controller.InsuranceController;
import ar.edu.itba.paw.webapp.controller.PatientController;

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

    private LinkDTO links;

    public static Function<Patient, PatientDTO> mapper(final UriInfo uriInfo){
        return (p) -> fromPatient(uriInfo, p);
    }

    public static PatientDTO fromPatient(final UriInfo uriInfo, Patient patient){
        final PatientDTO dto = new PatientDTO();

        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setTelephone(patient.getTelephone());
        dto.setBirthdate(patient.getBirthdate());
        if(patient.getBloodType()!=null) dto.setBloodtype(patient.getBloodType().getName());
        dto.setHeight(patient.getHeight());
        dto.setWeight(patient.getWeight());
        dto.setSmokes(patient.getSmokes());
        dto.setDrinks(patient.getDrinks());
        dto.setMeds(patient.getMeds());
        dto.setConditions(patient.getConditions());
        dto.setAllergies(patient.getAllergies());
        dto.setDiet(patient.getDiet());
        dto.setHobbies(patient.getHobbies());
        dto.setJob(patient.getJob());
        dto.setInsuranceNumber(patient.getInsuranceNumber());

        URI insurance = null;
        if(patient.getInsurance()!=null) insurance = uriInfo.getBaseUriBuilder().path(InsuranceController.class).path(String.valueOf(patient.getInsurance().getId())).build();
        URI doctors = uriInfo.getBaseUriBuilder().path(DoctorController.class).queryParam("patientId", patient.getId()).build();
        URI self = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).build();
        URI picture = uriInfo.getBaseUriBuilder().path(FileController.class).path(String.valueOf(patient.getPicture().getId())).build();

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setImage(picture)
            .setInsurance(insurance)
            .setDoctors(doctors)
        );

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

    public LinkDTO getLinks(){
        return links;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}
