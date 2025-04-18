package ar.edu.itba.paw.models;

import java.util.List;
import java.util.Objects;

public class DoctorView {
    private final long id;
    private final String name;
    private final SpecialtyEnum specialty;
    private List<Insurance> insurances = null;
    private List<WeekdayEnum> weekdays = null;

    public DoctorView(long id, String name, SpecialtyEnum specialty){
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    public DoctorView(long id, String name, SpecialtyEnum specialty, List<Insurance> insurances, List<WeekdayEnum> weekdays){
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.insurances = insurances;
        this.weekdays = weekdays;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DoctorView that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public SpecialtyEnum getSpecialty(){
        return specialty;
    }

    public List<Insurance> getInsurances(){
        return insurances;
    }



    public List<WeekdayEnum> getWeekdays(){
        return weekdays;
    }

    public void setInsurances(List<Insurance> insurances){
        this.insurances = insurances;
    }

    public void setWeekdays(List<WeekdayEnum> weekdays){
        this.weekdays = weekdays;
    }
}
