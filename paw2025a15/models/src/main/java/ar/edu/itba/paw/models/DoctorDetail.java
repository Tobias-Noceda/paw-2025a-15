package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;

public class DoctorDetail {
    private final long doctorId;
    private final String licence;
    private final SpecialtyEnum specialty;

    public DoctorDetail(long doctorId, String licence, SpecialtyEnum specialty){
        this.doctorId = doctorId;
        this.licence = licence;
        this.specialty = specialty;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public String getLicence(){
        return licence;
    }

    public SpecialtyEnum getSpecialty(){
        return specialty;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorDetail)) return false;

        DoctorDetail o = (DoctorDetail) other;

        return (this.doctorId==o.doctorId) 
        && (this.licence.equals(o.licence)) && (this.specialty.equals(o.specialty));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(doctorId);
        result = 31 * result + licence.hashCode();
        result = 31 * result + specialty.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "DoctorDetails{" +
            "doctorId=" + doctorId +
            ", licence=" + licence +
            ", specialty=" + specialty +
            '}';
    }
}
