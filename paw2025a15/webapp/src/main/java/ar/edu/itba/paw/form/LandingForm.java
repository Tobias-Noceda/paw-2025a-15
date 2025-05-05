package ar.edu.itba.paw.form;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class LandingForm {
    private String query;
    private SpecialtyEnum specialty;
    private Long insurances; // <-- ID en lugar del objeto
    private WeekdayEnum weekday;

    // Getters
    public String getQuery() {
        return query;
    }

    public SpecialtyEnum getSpecialty() {
        return specialty;
    }

    public Long getInsurances() {
        return insurances;
    }

    public WeekdayEnum getWeekday() {
        return weekday;
    }

    // Setter
    public void setQuery(String query) {
        this.query = escapeLikeSpecialChars(query);
    }

    public void setSpecialty(SpecialtyEnum specialty) {
        this.specialty = specialty;
    }

    public void setInsurances(Long insurances) {
        this.insurances = insurances;
    }

    public void setWeekday(WeekdayEnum weekday) {
        this.weekday = weekday;
    }

    @Override
    public String toString() {
        return "LandingForm{" +
                "query=" + query +
                "specialty=" + specialty +
                ", insurances=" + insurances +
                ", weekday=" + weekday +
                '}';
    }

    private String escapeLikeSpecialChars(String input) {
        if (input == null) return null;
        return input
                .replace("\\", "\\\\")  // Escape backslash first!
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
