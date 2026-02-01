package ar.edu.itba.paw.webapp.dto.output;

import java.util.List;

public class VacationsResponseDTO {
    
    private List<DoctorVacationDTO> future;
    private List<DoctorVacationDTO> past;

    public VacationsResponseDTO() {
    }

    public VacationsResponseDTO(List<DoctorVacationDTO> future, List<DoctorVacationDTO> past) {
        this.future = future;
        this.past = past;
    }

    public List<DoctorVacationDTO> getFuture() {
        return future;
    }

    public void setFuture(List<DoctorVacationDTO> future) {
        this.future = future;
    }

    public List<DoctorVacationDTO> getPast() {
        return past;
    }

    public void setPast(List<DoctorVacationDTO> past) {
        this.past = past;
    }
}
