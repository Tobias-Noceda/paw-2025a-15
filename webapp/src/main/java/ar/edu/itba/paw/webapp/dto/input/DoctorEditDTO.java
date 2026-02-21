package ar.edu.itba.paw.webapp.dto.input;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;
import ar.edu.itba.paw.webapp.form.constraints.ValidArgPhone;

@NonEmptyBody
public class DoctorEditDTO {

    @URL(protocol = "http")
    private String picture;

    @ValidArgPhone
    private String telephone;

    @Pattern(regexp = "ES_AR|ES_US|EN_AR|EN_US")
    private String mailLanguage;

    @Size(min = 1)
    private List<@URL(protocol = "http")String> insurances;

    private Boolean updateSchedule;

    private Boolean keepTurns;

    @Valid
    private ShiftsModificationDTO shifts;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

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

    public List<String> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<String> insurances) {
        this.insurances = insurances;
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
