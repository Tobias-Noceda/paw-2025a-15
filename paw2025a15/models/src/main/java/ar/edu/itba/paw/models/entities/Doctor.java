package ar.edu.itba.paw.models.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import ar.edu.itba.paw.models.Schedule;
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

    @OneToMany(orphanRemoval = false, fetch = FetchType.LAZY)
    @JoinTable(
        name = "doctor_coverages",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "insurance_id")
    )
    private List<Insurance> insurances;

    @OneToMany(orphanRemoval = false, mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<DoctorSingleShift> singleShifts;

    @OneToMany(orphanRemoval = true, mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<DoctorVacation> vacations;

    public Doctor() {
        super();
    }
    
    public Doctor(
        String email,
        String password,
        String name,
        String telephone,
        File picture,
        LocalDate creatDate,
        LocaleEnum locale,
        String licence,
        SpecialtyEnum specialty,
        List<Insurance> insurances
    ) {
        super(email, password, name, telephone, UserRoleEnum.DOCTOR, picture, creatDate, locale);
        this.licence = licence;
        this.specialty = specialty;
        this.insurances = insurances != null ? insurances : new ArrayList<>();
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

    public void addInsurance(Insurance insurance) {
        if (insurances == null) {
            insurances = new ArrayList<>();
        }
        if (insurance != null && !insurances.contains(insurance)) {
            insurances.add(insurance);
        }
    }

    public void removeInsurance(Insurance insurance) {
        if (insurances != null && insurance != null) {
            insurances.remove(insurance);
        }
    }

    public List<DoctorSingleShift> getSingleShifts() {
        return singleShifts.stream()
                .filter(shift -> shift.getIsActive())
                .toList();
    }

    public Schedule getSchedules(){
        List<DoctorSingleShift> shifts = getSingleShifts();
        List<WeekdayEnum> weekdays = new ArrayList<>();
        for(DoctorSingleShift shift : shifts){
            if(!weekdays.contains(shift.getWeekday()))
                weekdays.add(shift.getWeekday());
        }
            Schedule schedule = new Schedule(
                    weekdays,
                    shifts.getFirst().getStartTime().toString(),
                    shifts.getLast().getEndTime().toString(),
                    shifts.getFirst().getAddress(),
                    0
            );
        return schedule;
    }

    public void setSingleShifts(List<DoctorSingleShift> singleShifts) {
        this.singleShifts = singleShifts;
    }

    public void addSingleShift(DoctorSingleShift singleShift) {
        if (singleShifts == null) {
            singleShifts = new ArrayList<>();
        }
        if (singleShift != null && !singleShifts.contains(singleShift)) {
            singleShifts.add(singleShift);
        }
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

    public List<DoctorVacation> getVacations() {
        return vacations;
    }

    public void setVacations(List<DoctorVacation> vacations) {
        this.vacations = vacations;
    }

    public void addVacation(DoctorVacation vacation) {
        if (vacations == null) {
            vacations = new ArrayList<>();
        }
        if (vacation != null && !vacations.contains(vacation)) {
            vacations.add(vacation);
        }
    }
}
