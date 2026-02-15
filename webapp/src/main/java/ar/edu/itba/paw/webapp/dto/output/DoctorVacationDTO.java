package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.webapp.controller.DoctorController;

public class DoctorVacationDTO {

    private String startDate;
    private String endDate;

    private LinkDTO links;

    public static Function<DoctorVacation, DoctorVacationDTO> mapper(final UriInfo uriInfo) {
        return (dv) -> fromVacation(uriInfo, dv);
    }

    public static DoctorVacationDTO fromVacation(final UriInfo uriInfo, DoctorVacation vacation) {
        final DoctorVacationDTO dto = new DoctorVacationDTO();

        dto.startDate = vacation.getId().getStartDate().toString();
        dto.endDate = vacation.getId().getEndDate().toString();

        URI self = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(vacation.getDoctor().getId())).path("vacations").path(dto.getStartDate()).path(dto.getEndDate()).build();
        URI doctor = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(vacation.getDoctor().getId())).build();

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setDoctor(doctor)
        );

        return dto;
    }

    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public LinkDTO getLinks(){
        return links;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}
