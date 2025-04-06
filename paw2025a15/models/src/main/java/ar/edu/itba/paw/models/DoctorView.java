package ar.edu.itba.paw.models;

import java.util.List;

public class DoctorView {
    private long id;
    private String name;
    private SpecialtyEnum specialty;
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
