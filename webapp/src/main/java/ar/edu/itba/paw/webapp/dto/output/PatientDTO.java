package ar.edu.itba.paw.webapp.dto.output;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.webapp.controller.AppointmentController;
import ar.edu.itba.paw.webapp.controller.DoctorController;
import ar.edu.itba.paw.webapp.controller.FileController;
import ar.edu.itba.paw.webapp.controller.InsuranceController;
import ar.edu.itba.paw.webapp.controller.PatientController;

public class PatientDTO {
    // BASIC
    private String email;
    private String name;
    private String telephone;
    private LocalDate birthdate;
    private String bloodType;
    private BigDecimal height;
    private BigDecimal weight;
    private String insuranceNumber;
    private String mailLanguage;
    // HABITS
    private Boolean smokes;
    private Boolean drinks;
    private String diet;
    // MEDICAL
    private String meds;
    private String conditions;
    private String allergies;
    // SOCIAL
    private String hobbies;
    private String job;

    private LinkDTO links;

    public static Function<Patient, PatientDTO> mapper(final UriInfo uriInfo) {
        return (p) -> fromPatient(uriInfo, p);
    }

    public static PatientDTO fromPatient(final UriInfo uriInfo, Patient patient) {
        final PatientDTO dto = new PatientDTO();

        dto.name = patient.getName();
        dto.email = patient.getEmail();
        dto.telephone = patient.getTelephone();
        dto.birthdate = patient.getBirthdate();
        dto.bloodType = patient.getBloodType() != null ? patient.getBloodType().getName() : null;
        dto.height = patient.getHeight();
        dto.weight = patient.getWeight();
        dto.insuranceNumber = patient.getInsuranceNumber();
        dto.mailLanguage = patient.getLocale() != null ? patient.getLocale().name() : null;
        dto.smokes = patient.getSmokes();
        dto.drinks = patient.getDrinks();
        dto.diet = patient.getDiet();
        dto.meds = patient.getMeds();
        dto.conditions = patient.getConditions();
        dto.allergies = patient.getAllergies();
        dto.hobbies = patient.getHobbies();
        dto.job = patient.getJob();

        URI baseInsurance = patient.getInsurance() != null ? uriInfo.getBaseUriBuilder().path(InsuranceController.class)
                .path(String.valueOf(patient.getInsurance().getId())).build() : null;
        TemplatedLinkDTO insurance = TemplatedLinkDTO.of(baseInsurance);
        URI baseDoctors = uriInfo.getBaseUriBuilder().path(DoctorController.class).queryParam("patientId", patient.getId())
                .build();
        TemplatedLinkDTO doctors = TemplatedLinkDTO.of(baseDoctors);
        URI baseSelf = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId()))
                .build();
        TemplatedLinkDTO self = TemplatedLinkDTO.of(baseSelf);
        URI basePicture = uriInfo.getBaseUriBuilder().path(FileController.class)
                .path(String.valueOf(patient.getPicture().getId())).build();
        TemplatedLinkDTO picture = TemplatedLinkDTO.of(basePicture);
        URI basePastAppointments = uriInfo.getBaseUriBuilder().path(AppointmentController.class)
                .queryParam("userId", patient.getId()).queryParam("status", AppointmentStatusEnum.COMPLETED).build();
        TemplatedLinkDTO pastAppointments = TemplatedLinkDTO.of(basePastAppointments);
        URI baseFutureAppointments = uriInfo.getBaseUriBuilder().path(AppointmentController.class)
                .queryParam("userId", patient.getId()).queryParam("status", AppointmentStatusEnum.TAKEN).build();
        TemplatedLinkDTO futureAppointments = TemplatedLinkDTO.of(baseFutureAppointments);
        URI baseMedicalInfo = uriInfo.getBaseUriBuilder().path(PatientController.class)
                .path(String.valueOf(patient.getId())).path("medicalInfo").build();
        TemplatedLinkDTO medicalInfo = TemplatedLinkDTO.of(baseMedicalInfo);
        URI baseSocialInfo = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId()))
                .path("socialInfo").build();
        TemplatedLinkDTO socialInfo = TemplatedLinkDTO.of(baseSocialInfo);
        URI baseHabitsInfo = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId()))
                .path("habitsInfo").build();
        TemplatedLinkDTO habitsInfo = TemplatedLinkDTO.of(baseHabitsInfo);
        URI baseStudies = uriInfo.getBaseUriBuilder().path(PatientController.class)
                .path(String.valueOf(patient.getId())).path("studies").build();
        TemplatedLinkDTO studies = TemplatedLinkDTO.withQueryParams(baseStudies, List.of("doctorId"));

        dto.setLinks(new LinkDTO()
                .setSelf(self)
                .setMedicalInfo(medicalInfo)
                .setSocialInfo(socialInfo)
                .setHabitsInfo(habitsInfo)
                .setImage(picture)
                .setInsurance(insurance)
                .setDoctors(doctors)
                .setPastAppointments(pastAppointments)
                .setFutureAppointments(futureAppointments)
                .setStudies(studies));

        return dto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
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

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getMailLanguage() {
        return mailLanguage;
    }

    public void setMailLanguage(String mailLanguage) {
        this.mailLanguage = mailLanguage;
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

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
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

    public LinkDTO getLinks() {
        return links;
    }

    public void setLinks(LinkDTO links) {
        this.links = links;
    }
}
