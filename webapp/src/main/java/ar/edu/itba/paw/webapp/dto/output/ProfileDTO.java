package ar.edu.itba.paw.webapp.dto.output;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

import javax.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.webapp.controller.FileController;
import ar.edu.itba.paw.webapp.controller.InsuranceController;
import ar.edu.itba.paw.webapp.controller.ProfileController;

public class ProfileDTO {

    private String name;
    private String email;
    private String role;
    private String telephone;
    private String mailLanguage;
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
    private String insuranceName;
    private Long insuranceId;
    private String insuranceNumber;
    private String licence;
    private String specialty;
    private List<String> insurances;
    private String address;

    private LinkDTO links;

    public static ProfileDTO fromPatient(final UriInfo uriInfo, final Patient patient) {
        final ProfileDTO dto = new ProfileDTO();
        final User user = patient;

        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setTelephone(user.getTelephone());
        dto.setMailLanguage(user.getLocale().name());

        dto.setBirthdate(patient.getBirthdate());
        if (patient.getBloodType() != null) {
            dto.setBloodtype(patient.getBloodType().getName());
        }
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
        if (patient.getInsurance() != null) {
            dto.setInsuranceId(patient.getInsurance().getId());
            dto.setInsuranceName(patient.getInsurance().getName());
            insurance = uriInfo.getBaseUriBuilder()
                .path(InsuranceController.class)
                .path(String.valueOf(patient.getInsurance().getId()))
                .build();
        }

        URI self = uriInfo.getBaseUriBuilder().path(ProfileController.class).build();
        URI picture = uriInfo.getBaseUriBuilder()
            .path(FileController.class)
            .path(String.valueOf(user.getPicture().getId()))
            .build();

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setImage(picture)
            .setInsurance(insurance)
        );

        return dto;
    }

    public static ProfileDTO fromDoctor(final UriInfo uriInfo, final Doctor doctor) {
        final ProfileDTO dto = new ProfileDTO();
        final User user = doctor;

        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setTelephone(user.getTelephone());
        dto.setMailLanguage(user.getLocale().name());

        dto.setLicence(doctor.getLicence());
        if (doctor.getSpecialty() != null) {
            dto.setSpecialty(doctor.getSpecialty().name());
        }

        List<String> insuranceNames = new ArrayList<>();
        if (doctor.getInsurances() != null) {
            insuranceNames = doctor.getInsurances().stream()
                .map(i -> i.getName())
                .distinct()
                .toList();
        }
        dto.setInsurances(insuranceNames);

        String shiftAddress = null;
        if (doctor.getSingleShifts() != null) {
            shiftAddress = doctor.getSingleShifts().stream()
                .filter(DoctorSingleShift::getIsActive)
                .map(DoctorSingleShift::getAddress)
                .findFirst()
                .orElse(null);
        }
        dto.setAddress(shiftAddress);

        URI self = uriInfo.getBaseUriBuilder().path(ProfileController.class).build();
        URI picture = uriInfo.getBaseUriBuilder()
            .path(FileController.class)
            .path(String.valueOf(user.getPicture().getId()))
            .build();

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setImage(picture)
        );

        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMailLanguage() {
        return mailLanguage;
    }

    public void setMailLanguage(String mailLanguage) {
        this.mailLanguage = mailLanguage;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Boolean getSmokes() {
        return smokes;
    }

    public void setSmokes(Boolean smokes) {
        this.smokes = smokes;
    }

    public Boolean getDrinks() {
        return drinks;
    }

    public void setDrinks(Boolean drinks) {
        this.drinks = drinks;
    }

    public String getMeds() {
        return meds;
    }

    public void setMeds(String meds) {
        this.meds = meds;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public Long getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Long insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public List<String> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<String> insurances) {
        this.insurances = insurances;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LinkDTO getLinks() {
        return links;
    }

    public void setLinks(LinkDTO links) {
        this.links = links;
    }
}
