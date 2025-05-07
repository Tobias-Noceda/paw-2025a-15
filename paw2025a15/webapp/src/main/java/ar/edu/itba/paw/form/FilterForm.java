package ar.edu.itba.paw.form;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Deprecated
public class FilterForm {
    private SpecialtyEnum specialty;
    private Long insurances; // <-- ID en lugar del objeto
    private WeekdayEnum weekday;

    public SpecialtyEnum getSpecialty() {
        return specialty;
    }

    public void setSpecialty(SpecialtyEnum specialty) {
        this.specialty = specialty;
    }

    public Long getInsurances() {
        return insurances;
    }

    public void setInsurances(Long insurances) {
        this.insurances = insurances;
    }

    public WeekdayEnum getWeekday() {
        return weekday;
    }

    public void setWeekday(WeekdayEnum weekday) {
        this.weekday = weekday;
    }

    @Override
    public String toString() {
        return "FilterForm{" +
                "specialty=" + specialty +
                ", insurances=" + insurances +
                ", weekday=" + weekday +
                '}';
    }
}

