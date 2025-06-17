package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.webapp.form.VacationForm;
import ar.edu.itba.paw.webapp.form.constraints.ValidVacation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class VacationValidator implements ConstraintValidator<ValidVacation, VacationForm> {

    @Override
    public boolean isValid(VacationForm form, ConstraintValidatorContext context) {
        if (form.getStartDate() == null || form.getEndDate() == null) {
            return false;
        }
        return form.getEndDate().isAfter(form.getStartDate());
    }
}