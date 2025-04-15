package ar.edu.itba.paw.form.constraints;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;

public class AvailableTurnValidator implements ConstraintValidator<AvailableTurn, TakeTurnForm>{

    private final AppointmentService as;

    @Autowired
    public AvailableTurnValidator(AppointmentService as){
        this.as = as;
    }

    @Override
    public boolean isValid(TakeTurnForm form, ConstraintValidatorContext context){
        return as.getAppointmentsByShiftIdAndDate(form.getShiftId(), LocalDate.parse(form.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))).isEmpty();
    }
}