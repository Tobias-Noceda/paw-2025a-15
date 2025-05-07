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
}
