package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class InsuranceCreateDTO {
    
    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Long pictureId;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Long getPictureId(){
        return pictureId;
    }

    public void setPictureId(Long pictureId){
        this.pictureId = pictureId;
    }
}
