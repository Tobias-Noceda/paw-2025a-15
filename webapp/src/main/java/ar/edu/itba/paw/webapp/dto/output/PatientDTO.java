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
    //BASIC
    private String email;
    private String name;
    private String telephone;
    private LocalDate birthdate;
    private String bloodType;
    private BigDecimal height;
    private BigDecimal weight;
    private String insuranceNumber;

    private LinkDTO links;

    public static Function<Patient, PatientDTO> mapper(final UriInfo uriInfo){
        return (p) -> fromPatient(uriInfo, p);
    }

    public static PatientDTO fromPatient(final UriInfo uriInfo, Patient patient){
        final PatientDTO dto = new PatientDTO();

        dto.name = patient.getName();
        dto.email = patient.getEmail();
        dto.telephone = patient.getTelephone();
        dto.birthdate = patient.getBirthdate();
        dto.bloodType = patient.getBloodType() != null ? patient.getBloodType().getName() : null;
        dto.height = patient.getHeight();
        dto.weight = patient.getWeight();
        dto.insuranceNumber = patient.getInsuranceNumber();

        URI insurance = patient.getInsurance() != null ? uriInfo.getBaseUriBuilder().path(InsuranceController.class).path(String.valueOf(patient.getInsurance().getId())).build() : null;
        URI doctors = uriInfo.getBaseUriBuilder().path(DoctorController.class).queryParam("patientId", patient.getId()).build();
        URI self = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).build();
        URI picture = uriInfo.getBaseUriBuilder().path(FileController.class).path(String.valueOf(patient.getPicture().getId())).build();
        URI pastAppointments = uriInfo.getBaseUriBuilder().path(AppointmentController.class).queryParam("userId", patient.getId()).queryParam("status", AppointmentStatusEnum.COMPLETED).build();
        URI futureAppointments = uriInfo.getBaseUriBuilder().path(AppointmentController.class).queryParam("userId", patient.getId()).queryParam("status", AppointmentStatusEnum.TAKEN).build();
        URI medicalInfo = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).path("medicalInfo").build();
        URI socialInfo = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).path("socialInfo").build();
        URI habitsInfo = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).path("habitsInfo").build();

        URI baseStudies = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).path("studies").build();
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
            .setStudies(studies)
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

    public LocalDate getBirthdate(){
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate){
        this.birthdate = birthdate;
    }

    public String getBloodType(){
        return bloodType;
    }

    public void setBloodType(String bloodType){
        this.bloodType = bloodType;
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
