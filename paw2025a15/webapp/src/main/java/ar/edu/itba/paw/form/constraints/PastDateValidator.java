package ar.edu.itba.paw.form.constraints;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PastDateValidator implements ConstraintValidator<PastDate, LocalDate>{

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context){

        return date == null || !date.isAfter(LocalDate.now());
    }
}
