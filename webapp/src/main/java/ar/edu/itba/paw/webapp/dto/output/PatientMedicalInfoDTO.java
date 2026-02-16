package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.webapp.controller.PatientController;

public class PatientMedicalInfoDTO {
    
    private String meds;
    private String conditions;
    private String allergies;

    private LinkDTO links;

    public static PatientMedicalInfoDTO fromPatient(final UriInfo uriInfo, Patient patient){
        final PatientMedicalInfoDTO dto = new PatientMedicalInfoDTO();

        dto.setMeds(patient.getMeds());
        dto.setConditions(patient.getConditions());
        dto.setAllergies(patient.getAllergies());

        URI baseSelf = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).path("medicalInfo").build();
        TemplatedLinkDTO self = TemplatedLinkDTO.of(baseSelf);

        dto.setLinks(new LinkDTO()
            .setSelf(self)
        );

        return dto;
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

    public LinkDTO getLinks(){
        return links;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}