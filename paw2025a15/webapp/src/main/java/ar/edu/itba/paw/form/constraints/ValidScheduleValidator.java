package ar.edu.itba.paw.form.constraints;

import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.models.Schedule;

public class ValidScheduleValidator implements ConstraintValidator<ValidSchedule, Schedule> {

    @Override
    public boolean isValid(Schedule schedule, ConstraintValidatorContext context) {
        if (schedule == null) return false;

        if(schedule.getWeekday() == null || schedule.getWeekday().isEmpty() || 
            schedule.getStartTime() != null || schedule.getEndTime() != null) {
            return false;
        }
        else if(LocalDateTime.parse(schedule.getStartTime()).isBefore(LocalDateTime.parse(schedule.getEndTime()))){
            return true;
        }

        return false;
    }
}