package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.webapp.controller.FileController;
import ar.edu.itba.paw.webapp.controller.InsuranceController;

public class InsuranceDTO {
    
    private String name;

    private LinkDTO links;

    public static Function<Insurance, InsuranceDTO> mapper(final UriInfo uriInfo){
        return (i) -> fromInsurance(uriInfo, i);
    }

    public static InsuranceDTO fromInsurance(final UriInfo uriInfo, Insurance insurance){
        final InsuranceDTO dto = new InsuranceDTO();

        dto.setName(insurance.getName());
        
        URI self = uriInfo.getBaseUriBuilder().path(InsuranceController.class).path(String.valueOf(insurance.getId())).build();
        URI picture = uriInfo.getBaseUriBuilder().path(FileController.class).path(String.valueOf(insurance.getPicture().getId())).build();

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setImage(picture)
        );

        return dto;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public LinkDTO getLinks(){
        return links;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}
