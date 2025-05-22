package ar.edu.itba.paw.models;

import java.time.LocalTime;

import ar.edu.itba.paw.models.enums.WeekdayEnum;

import javax.persistence.*;

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

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private User doctor;

    @Enumerated
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

    public User getDoctor(){
        return doctor;
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

        return (this.id==o.id) && (this.doctor.equals(o.doctor))
        && (this.weekday.equals(o.weekday)) && (this.address.equals(o.address))
        && (this.startTime.equals(o.startTime)) && (this.endTime.equals(o.endTime));
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
