package ar.edu.itba.paw.models.entities;

import java.time.LocalTime;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ar.edu.itba.paw.models.enums.WeekdayEnum;

// TODO: Delete when we are sure
@Entity
@Table(name = "doctor_shifts",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"doctor_id", "shift_weekday", "shift_start_time"}),
        @UniqueConstraint(columnNames = {"doctor_id", "shift_weekday", "shift_end_time"})
    }
)
public class DoctorShift {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doctor_shifts_shift_id_seq")
    @SequenceGenerator(sequenceName = "doctor_shifts_shift_id_seq", name = "doctor_shifts_shift_id_seq", allocationSize = 1)
    @Column(name = "shift_id")
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private User doctor;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "shift_weekday", nullable = false)
    private WeekdayEnum weekday;

    @Column(name = "shift_address", length = 50, nullable = false)
    private String address;

    @Column(name = "shift_start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "shift_end_time", nullable = false)
    private LocalTime endTime;

    public DoctorShift(){
        //just for hibernate
    }

    public DoctorShift(User doctor, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime){
        this.doctor = doctor;
        this.weekday = weekday;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public User getDoctor(){
        return doctor;
    }

    public void setDoctor(User doctor){
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

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorShift)) return false;

        DoctorShift o = (DoctorShift) other;

        return (this.id==o.id);
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
            '}';
    }
}
