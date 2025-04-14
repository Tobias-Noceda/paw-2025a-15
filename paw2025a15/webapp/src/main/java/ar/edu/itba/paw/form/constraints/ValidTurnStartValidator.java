package ar.edu.itba.paw.form.constraints;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.form.TakeTurnForm;

public class ValidTurnStartValidator implements ConstraintValidator<ValidTurnStart, TakeTurnForm>{

    @Override
    public boolean isValid(TakeTurnForm form, ConstraintValidatorContext context){
        if(form.getDate() == null || form.getStartTime() == null) return false;
        LocalDate localDate = LocalDate.parse(form.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime localTime = LocalTime.parse(form.getStartTime().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime startTime = LocalDateTime.of(localDate, localTime);
        return LocalDateTime.now().isBefore(startTime);
    }
}
