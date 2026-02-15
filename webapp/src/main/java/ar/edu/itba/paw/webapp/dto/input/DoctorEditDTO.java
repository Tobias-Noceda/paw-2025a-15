package ar.edu.itba.paw.webapp.dto.input;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;
import ar.edu.itba.paw.webapp.form.constraints.ValidArgPhone;

@NonEmptyBody
public class DoctorEditDTO {

    @ValidArgPhone
    private String telephone;

    @Pattern(regexp = "ES_AR|ES_US|EN_AR|EN_US")
    private String mailLanguage;

    private List<Long> insuranceIds;

    private Boolean updateSchedule;

    private Boolean keepTurns;

    @Valid
    private ShiftsModificationDTO shifts;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMailLanguage() {
        return mailLanguage;
    }

    public void setMailLanguage(String mailLanguage) {
        this.mailLanguage = mailLanguage;
    }

    public List<Long> getInsuranceIds() {
        return insuranceIds;
    }

    public void setInsuranceIds(List<Long> insuranceIds) {
        this.insuranceIds = insuranceIds;
    }

    public Boolean getUpdateSchedule() {
        return updateSchedule;
    }

    public void setUpdateSchedule(Boolean updateSchedule) {
        this.updateSchedule = updateSchedule;
    }

    public Boolean getKeepTurns() {
        return keepTurns;
    }

    public void setKeepTurns(Boolean keepTurns) {
        this.keepTurns = keepTurns;
    }

    public ShiftsModificationDTO getShifts() {
        return shifts;
    }

    public void setShifts(ShiftsModificationDTO shifts) {
        this.shifts = shifts;
    }
}
