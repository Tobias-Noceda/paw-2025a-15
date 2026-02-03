package ar.edu.itba.paw.webapp.dto.input;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.webapp.form.constraints.ValidArgPhone;
import ar.edu.itba.paw.webapp.form.constraints.ValidLicence;

public class DoctorCreateDTO {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Min(8)
    private String password;

    @ValidArgPhone
    private String telephone;

    @Pattern(regexp = "^\\d{1,15}$")
    @ValidLicence
    private String license;

    @NotNull
    @NotBlank
    private SpecialtyEnum specialty;

    @NotNull
    private List<String> insurances;

    @Valid
    private ShiftsModificationDTO shifts;

    // Getters
    public String getName(){
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getLicense() {
        return license;
    }

    public SpecialtyEnum getSpecialty() {
        return specialty;
    }

    public List<String> getInsurances() {
        return insurances;
    }

    public ShiftsModificationDTO getShifts() {
        return shifts;
    }

    // Setters
    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setSpecialty(SpecialtyEnum specialty) {
        this.specialty = specialty;
    }

    public void setInsurances(List<String> insurances) {
        this.insurances = insurances;
    }

    public void setShifts(ShiftsModificationDTO shifts) {
        this.shifts = shifts;
    }
}
