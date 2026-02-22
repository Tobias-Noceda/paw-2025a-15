package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.webapp.controller.PatientController;

public class PatientSocialInfoDTO {
    
    private String hobbies;
    private String job;

    private LinkDTO links;

    public static PatientSocialInfoDTO fromPatient(final UriInfo uriInfo, Patient patient){
        final PatientSocialInfoDTO dto = new PatientSocialInfoDTO();

        dto.setHobbies(patient.getHobbies());
        dto.setJob(patient.getJob());

        URI baseSelf = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(patient.getId())).path("socialInfo").build();
        TemplatedLinkDTO self = TemplatedLinkDTO.of(baseSelf);

        dto.setLinks(new LinkDTO()
            .setSelf(self)
        );

        return dto;
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

    public LinkDTO getLinks(){
        return links;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}
