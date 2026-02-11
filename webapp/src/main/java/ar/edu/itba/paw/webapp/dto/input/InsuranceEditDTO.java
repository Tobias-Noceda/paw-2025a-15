package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.URL;

import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;

@NonEmptyBody
public class InsuranceEditDTO {
    
    @NotBlank
    private String name;

    @URL(protocol = "http")
    private String pictureId;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPictureId(){
        return pictureId;
    }

    public void setPictureId(String pictureId){
        this.pictureId = pictureId;
    }
}
