package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.paw.webapp.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;

public class AvailableTurnValidator implements ConstraintValidator<AvailableTurn, TakeTurnForm>{

    private final AppointmentService as;

    @Autowired
    public AvailableTurnValidator(AppointmentService as){
        this.as = as;
    }

    @Override
    public boolean isValid(TakeTurnForm form, ConstraintValidatorContext context){
        return as.getAppointmentsByShiftIdAndDate(form.getShiftId(), form.getDate()).isEmpty();
    }
}