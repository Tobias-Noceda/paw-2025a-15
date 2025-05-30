package ar.edu.itba.paw.models.entities;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Entity
@Table(name = "doctor_details")
@PrimaryKeyJoinColumn(name = "doctor_id")
@DiscriminatorValue("1")
public class Doctor extends User {

    @Column(name = "doctor_licence", length = 50, nullable = false, unique = true)
    private String licence;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "doctor_specialty", nullable = false)
    private SpecialtyEnum specialty;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "doctor_coverages",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "insurance_id")
    )
    private List<Insurance> insurances;

    @OneToMany(orphanRemoval = true, mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<DoctorSingleShift> singleShifts;

    public Doctor() {
        super();
    }
    
    public Doctor(String email, String password, String name, String telephone, File picture, LocalDate createDate, LocaleEnum locale,
                  String licence, SpecialtyEnum specialty) {
        super(email, password, name, telephone, UserRoleEnum.DOCTOR, picture, createDate, locale);
        this.licence = licence;
        this.specialty = specialty;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public SpecialtyEnum getSpecialty() {
        return specialty;
    }

    public void setSpecialty(SpecialtyEnum specialty) {
        this.specialty = specialty;
    }

    public List<Insurance> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<Insurance> insurances) {
        this.insurances = insurances;
    }

    public List<DoctorSingleShift> getSingleShifts() {
        return singleShifts;
    }

    public void setSingleShifts(List<DoctorSingleShift> singleShifts) {
        this.singleShifts = singleShifts;
    }

    public List<WeekdayEnum> getAvailableDays() {
        return singleShifts.stream()
                .map(DoctorSingleShift::getWeekday)
                .distinct()
                .toList();
    }

    public List<String> getInsuranceNames() {
        return insurances.stream()
                .map(Insurance::getName)
                .distinct()
                .toList();
    }
}
