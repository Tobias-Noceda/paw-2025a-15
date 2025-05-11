package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.enums.SpecialtyEnum;

public class DoctorDetail {
    private final long doctorId;
    private final String doctorLicense;
    private final SpecialtyEnum specialty;

    public DoctorDetail(long doctorId, String doctorLicense, SpecialtyEnum specialty) {
        this.doctorId = doctorId;
        this.doctorLicense = doctorLicense;
        this.specialty = specialty;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public String getDoctorLicense() {
        return doctorLicense;
    }

    public SpecialtyEnum getSpecialty() {
        return specialty;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (!(other instanceof DoctorDetail)) return false;

        DoctorDetail o = (DoctorDetail) other;

        return (this.doctorId == o.doctorId)
                && (this.doctorLicense.equals(o.doctorLicense))
                && (this.specialty.equals(o.specialty));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(doctorId);
        result = 31 * result + doctorLicense.hashCode();
        result = 31 * result + specialty.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DoctorDetail{" +
                "doctorId=" + doctorId +
                ", doctorLicense='" + doctorLicense + '\'' +
                ", specialty=" + specialty +
                '}';
    }
}