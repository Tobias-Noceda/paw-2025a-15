package ar.edu.itba.paw.webapp.dto.input;

import java.time.LocalTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;

@NonEmptyBody
public class ShiftsModificationDTO {

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    @Range(min = 0, max = 60)
    private Integer duration;

    @NotNull
    @NotBlank
    private String address;

    @NotNull
    private List<WeekdayEnum> weekdays;

    // Getters
    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getAddress() {
        return address;
    }

    public List<WeekdayEnum> getWeekdays() {
        return weekdays;
    }

    // Setters
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWeekdays(List<WeekdayEnum> weekdays) {
        this.weekdays = weekdays;
    }

    @Override
    public String toString() {
        return "ShiftsModificationDTO [startTime=" + startTime + ", endTime=" + endTime + ", duration=" + duration + ", address=" + address
                + ", weekdays=" + weekdays + "]";
    }
}
