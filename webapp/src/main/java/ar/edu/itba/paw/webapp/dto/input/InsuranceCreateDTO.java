package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;
@NonEmptyBody
public class InsuranceCreateDTO {
    
    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @URL(protocol = "http")
    private String picture;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPicture(){
        return picture;
    }

    public void setPicture(String pictureId){
        this.picture = pictureId;
    }
}
