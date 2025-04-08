package ar.edu.itba.paw.form;

import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.WeekdayEnum;

public class FilterForm {
    private SpecialtyEnum specialty;

    private Insurance insurances;

    private WeekdayEnum weekday;

    public SpecialtyEnum getSpecialty() {
        return specialty;
    }

    public void setSpecialty(SpecialtyEnum specialty) {
        this.specialty = specialty;
    }

    public Insurance getInsurances() {
        return insurances;
    }

    public void setInsurances(Insurance insurances) {
        this.insurances = insurances;
    }

    public WeekdayEnum getWeekday() {
        return weekday;
    }

    public void setWeekday(WeekdayEnum weekday) {
        this.weekday = weekday;
    }

}
