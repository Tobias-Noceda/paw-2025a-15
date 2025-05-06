package ar.edu.itba.paw.form;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class LandingForm {
    private String query;
    private SpecialtyEnum specialty;
    private Long insurances; // <-- ID en lugar del objeto
    private WeekdayEnum weekday;
    private boolean mostRecent=true;
    private boolean mostPopular=false;

    // Getters
    public String getQuery() {
        return query;
    }

    public String getEscapedQuery() {
        return escapeLikeSpecialChars(query);
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

    public boolean getMostRecent() { return mostRecent; }

    public boolean getMostPopular() { return mostPopular; }

    // Setter
    public void setQuery(String query) { this.query = query; }

    public void setSpecialty(SpecialtyEnum specialty) { this.specialty = specialty; }

    public void setInsurances(Long insurances) { this.insurances = insurances; }

    public void setWeekday(WeekdayEnum weekday) { this.weekday = weekday; }

    public void setMostRecent(boolean mostRecent) { this.mostRecent = mostRecent; }

    public void setMostPopular(boolean mostPopular) { this.mostPopular = mostPopular; }

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
