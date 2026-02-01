package ar.edu.itba.paw.webapp.dto.output;

import java.util.function.Function;

import ar.edu.itba.paw.models.entities.DoctorVacation;

public class DoctorVacationDTO {

    private Long doctorId;
    private String startDate;
    private String endDate;

    public static Function<DoctorVacation, DoctorVacationDTO> mapper() {
        return DoctorVacationDTO::fromVacation;
    }

    public static DoctorVacationDTO fromVacation(DoctorVacation vacation) {
        final DoctorVacationDTO dto = new DoctorVacationDTO();

        dto.doctorId = vacation.getId().getDoctorId();
        dto.startDate = vacation.getId().getStartDate().toString();
        dto.endDate = vacation.getId().getEndDate().toString();

        return dto;
    }


    public Long getDoctorId() {
        return doctorId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }


    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
