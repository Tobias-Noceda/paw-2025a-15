package ar.edu.itba.paw.models.entities;

import java.time.LocalTime;

import ar.edu.itba.paw.models.enums.WeekdayEnum;

import javax.persistence.*;

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
    private long id;

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
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
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

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorSingleShift)) return false;

        DoctorSingleShift o = (DoctorSingleShift) other;

        return (this.id==o.id) && (this.doctor.equals(o.doctor))
        && (this.weekday.equals(o.weekday)) && (this.address.equals(o.address))
        && (this.startTime.equals(o.startTime)) && (this.endTime.equals(o.endTime))
        && (this.duration == o.duration);
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
}
