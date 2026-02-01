package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotNull;

public class VacationCreateDTO {

    @NotNull(message = "Start date is required")
    private String startDate;

    @NotNull(message = "End date is required")
    private String endDate;


    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }


    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
