package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

public class InsuranceCreateDTO {
    
    @NotNull
    @NotBlank
    private String name;

    @NotNull
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
