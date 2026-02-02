package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.webapp.controller.PatientController;

public class PatientHabitsInfoDTO {
    
    private Boolean smokes;
    private Boolean drinks;
    private String diet;

    private LinkDTO links;

    public static PatientHabitsInfoDTO fromPatient(final UriInfo uriInfo, Patient patient){
        final PatientHabitsInfoDTO dto = new PatientHabitsInfoDTO();

        dto.setSmokes(patient.getSmokes());
        dto.setDrinks(patient.getDrinks());
        dto.setDiet(patient.getDiet());

        URI self = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).path("habitsInfo").build();

        dto.setLinks(new LinkDTO()
            .setSelf(self)
        );

        return dto;
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

    public String getDiet(){
        return diet;
    }

    public void setDiet(String diet){
        this.diet = diet;
    }

    public LinkDTO getLinks(){
        return links;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }

}
