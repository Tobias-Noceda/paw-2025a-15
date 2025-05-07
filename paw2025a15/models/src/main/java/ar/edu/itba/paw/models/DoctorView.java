package ar.edu.itba.paw.models;

import java.util.List;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public class DoctorView {
    private final long id;
    private final String name;
    private final SpecialtyEnum specialty;
    private final long imageId;
    private final List<WeekdayEnum> weekdays;
    private final List<Insurance> insurances;

    public DoctorView(long id, String name, SpecialtyEnum specialty, long imageId, List<Insurance> insurances, List<WeekdayEnum> weekdays){
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.imageId = imageId;
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

    public List<WeekdayEnum> getWeekdays() {
        return weekdays;
    }

    public long getImageId(){
        return imageId;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorView)) return false;

        DoctorView o = (DoctorView) other;

        return (this.id==o.id) && (this.imageId==o.imageId)
        && (this.name.equals(o.name)) && (this.specialty.equals(o.specialty))
        && ((this.weekdays == null && o.weekdays == null) || (this.weekdays != null && this.weekdays.equals(o.weekdays))) 
        && ((this.insurances == null && o.insurances == null) || (this.insurances != null && this.insurances.equals(o.insurances)));
 }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + Long.hashCode(imageId);
        result = 31 * result + specialty.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (weekdays != null ? weekdays.hashCode() : 0);
        result = 31 * result + (insurances != null ? insurances.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        return "DoctorView{" +
            "id=" + id +
            "," + "imageId=" + imageId +
            "," + "specialty=" + specialty +
            "," + "name=" + name +
            "," + "insurances=" + insurances +
            "," + "weekdays=" + weekdays +
            '}';
    }
}
