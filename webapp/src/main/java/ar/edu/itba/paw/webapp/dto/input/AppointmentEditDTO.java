package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotBlank;

import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;

@NonEmptyBody
public class AppointmentEditDTO {
    @NotBlank
    private AppointmentStatusEnum status;

    private String description;

    public AppointmentStatusEnum getStatus(){
        return status;
    }

    public void setStatus(AppointmentStatusEnum status){
        this.status = status;
    }//TODO no falta get?

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
