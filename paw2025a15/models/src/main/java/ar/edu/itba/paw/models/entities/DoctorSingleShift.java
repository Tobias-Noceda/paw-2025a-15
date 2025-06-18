package ar.edu.itba.paw.models.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Entity
@Table(
    name = "doctor_single_shifts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"doctor_id", "shift_weekday", "shift_start_time", "shift_duration"}),
        @UniqueConstraint(columnNames = {"doctor_id", "shift_weekday", "shift_end_time", "shift_duration"})
    }
)
public class DoctorSingleShift {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doctor_shifts_shift_id_seq")
    @SequenceGenerator(sequenceName = "doctor_shifts_shift_id_seq", name = "doctor_shifts_shift_id_seq", allocationSize = 1)
    @Column(name = "shift_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "shift_weekday", nullable = false)
    private WeekdayEnum weekday;

    @Column(name = "shift_address", length = 50, nullable = false)
    private String address;

    @Column(name = "shift_start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "shift_end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "shift_duration", nullable = false)
    private int duration; // Duration in minutes

    @Column(name = "shift_is_active", nullable = false)
    private boolean isActive;

    @OneToMany(orphanRemoval = false, mappedBy = "shift", fetch = FetchType.LAZY)
    private List<AppointmentNew> appointments;

    public DoctorSingleShift(){
        //just for hibernate
    }

    public DoctorSingleShift(Doctor doctor, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime, int duration){
        this.doctor = doctor;
        this.weekday = weekday;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.isActive = true; // Default to active
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Doctor getDoctor(){
        return doctor;
    }

    public void setDoctor(Doctor doctor){
        this.doctor = doctor;
    }

    public WeekdayEnum getWeekday(){
        return weekday;
    }

    public void setWeekday(WeekdayEnum weekday){
        this.weekday = weekday;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime){
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime){
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public List<AppointmentNew> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentNew> appointments) {
        this.appointments = appointments;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorSingleShift)) return false;

        DoctorSingleShift o = (DoctorSingleShift) other;

        return doctor.getId().equals(o.doctor.getId()) &&
            weekday.equals(o.weekday) &&
            address.equals(o.address) &&
            startTime.equals(o.startTime) &&
            endTime.equals(o.endTime) &&
            duration == o.duration;
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + doctor.hashCode();
        result = 31 * result + weekday.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "DoctorShift{" +
            "id=" + id +
            "," + "doctor=" + doctor +
            "," + "weekday=" + weekday +
            "," + "address=" + address +
            "," + "startTime=" + startTime +
            "," + "endTime=" + endTime +
            "," + "duration=" + duration +
            '}';
    }

    public boolean isValidStartAndEndTime(LocalTime startTime, LocalTime endTime) {
        if(startTime == null || endTime == null || startTime.isAfter(endTime)) {
            return false;
        }
        LocalTime appointmentStart = this.startTime;
        while (appointmentStart.isBefore(this.endTime)) {
            if (appointmentStart.equals(startTime) || appointmentStart.equals(endTime.minusMinutes(this.duration))) {
                return true;
            }
            appointmentStart = appointmentStart.plusMinutes(this.duration);
        }
        return false;
    }

    public boolean isValidDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        return WeekdayEnum.fromString(date.getDayOfWeek().name()).equals(this.weekday);
    }
}
