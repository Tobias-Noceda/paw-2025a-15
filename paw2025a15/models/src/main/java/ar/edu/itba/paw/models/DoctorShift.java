package ar.edu.itba.paw.models;

import java.time.LocalTime;

import ar.edu.itba.paw.models.enums.WeekdayEnum;

import javax.persistence.*;

@Entity
@Table(name = "doctor_shifts")
public class DoctorShift {
    @Column(name = "shift_id")
    private final long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private final long doctorId;
    @Column(name = "shift_weekday")
    private final WeekdayEnum weekday;
    @Column(name = "shift_address")
    private final String address;
    @Column(name = "shift_start_time")
    private final LocalTime startTime;
    @Column(name = "shift_end_time")
    private final LocalTime endTime;

    public DoctorShift(long id, long doctorId, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime){
        this.id = id;
        this.doctorId = doctorId;
        this.weekday = weekday;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId(){
        return id;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public WeekdayEnum getWeekday(){
        return weekday;
    }

    public String getAddress(){
        return address;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof DoctorShift)) return false;

        DoctorShift o = (DoctorShift) other;

        return (this.id==o.id) && (this.doctorId==o.doctorId)
        && (this.weekday.equals(o.weekday)) && (this.address.equals(o.address))
        && (this.startTime.equals(o.startTime)) && (this.endTime.equals(o.endTime));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + Long.hashCode(doctorId);
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
            "," + "doctorId=" + doctorId +
            "," + "weekday=" + weekday +
            "," + "address=" + address +
            "," + "startTime=" + startTime +
            "," + "endTime=" + endTime +
            '}';
    }
}
