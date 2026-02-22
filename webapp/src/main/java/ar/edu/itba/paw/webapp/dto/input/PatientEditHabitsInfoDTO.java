package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.Size;

public class PatientEditHabitsInfoDTO {
    
    private Boolean smokes;

    private Boolean drinks;

    @Size(max = 100)
    private String diet;

    public Boolean getSmokes() {
        return smokes;
    }

    public void setSmokes(Boolean smokes) {
        this.smokes = smokes;
    }

    public Boolean getDrinks() {
        return drinks;
    }

    public void setDrinks(Boolean drinks) {
        this.drinks = drinks;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }
}
