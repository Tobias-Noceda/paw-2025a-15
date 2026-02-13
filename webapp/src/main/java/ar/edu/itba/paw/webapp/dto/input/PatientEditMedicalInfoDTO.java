package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.Size;

public class PatientEditMedicalInfoDTO {

    @Size(max = 250)
    private String meds;

    @Size(max = 250)
    private String conditions;

    @Size(max = 250)
    private String allergies;

    public String getMeds() {
        return meds;
    }

    public void setMeds(String meds) {
        this.meds = meds;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
}
