package ar.edu.itba.paw.webapp.dto;

import java.net.URI;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Insurance;

public class InsuranceDTO {
    
    private String name;
    private URI self;
    private URI picture;
    private Long pictureId;

    public static Function<Insurance, InsuranceDTO> mapper(final UriInfo uriInfo){
        return (i) -> fromInsurance(uriInfo, i);
    }

    public static InsuranceDTO fromInsurance(final UriInfo uriInfo, Insurance insurance){
        final InsuranceDTO dto = new InsuranceDTO();

        dto.name = insurance.getName();
        dto.self = uriInfo.getBaseUriBuilder().path("insurances").path(String.valueOf(insurance.getId())).build();
        dto.picture = uriInfo.getBaseUriBuilder().path("images").path(String.valueOf(insurance.getPicture().getId())).build();

        return dto;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
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

    public Long getPictureId(){
        return pictureId;
    }

    public void setPictureId(Long pictureId){
        this.pictureId = pictureId;
    }
}
