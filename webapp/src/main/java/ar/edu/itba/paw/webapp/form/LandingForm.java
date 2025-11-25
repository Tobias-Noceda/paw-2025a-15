package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class LandingForm {
    private String query;
    private SpecialtyEnum specialty;
    private Long insurances; // <-- ID en lugar del objeto
    private WeekdayEnum weekday;
    private DoctorOrderEnum orderBy;

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

    public DoctorOrderEnum getOrderBy() {
        return orderBy;
    }


    // Setter

    public void setOrderBy(DoctorOrderEnum orderBy) {
        this.orderBy = orderBy;
    }
    public void setQuery(String query) { this.query = query; }

    public void setSpecialty(SpecialtyEnum specialty) { this.specialty = specialty; }

    public void setInsurances(Long insurances) { this.insurances = insurances; }

    public void setWeekday(WeekdayEnum weekday) { this.weekday = weekday; }


    @Override
    public String toString() {
        return "LandingForm{" +
                "query=" + query +
                "specialty=" + specialty +
                ", insurances=" + insurances +
                ", weekday=" + weekday +
                '}';
    }
}
