package ar.edu.itba.paw.form.constraints;

import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.models.Schedule;

public class ValidScheduleValidator implements ConstraintValidator<ValidSchedule, DoctorForm> {

    @Override
    public boolean isValid(DoctorForm form, ConstraintValidatorContext context) {
        if (form.getSchedules() == null) return false;

        Schedule schedule = form.getSchedules();

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