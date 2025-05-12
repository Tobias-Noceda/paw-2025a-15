package ar.edu.itba.paw.webapp.form.constraints;

import java.time.LocalTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.models.Schedule;

public class ValidScheduleValidator implements ConstraintValidator<ValidSchedule, Schedule> {

    @Override
    public boolean isValid(Schedule schedule, ConstraintValidatorContext context) {
        if (schedule == null) {
            return false;
        }

        if(schedule.getWeekday() == null || schedule.getWeekday().isEmpty() || 
            schedule.getStartTime() == null || schedule.getEndTime() == null) {
            return false;
        }
        else if(!LocalTime.parse(schedule.getStartTime()).isBefore(LocalTime.parse(schedule.getEndTime()))){
            return false;
        }
        return true;
    }
}