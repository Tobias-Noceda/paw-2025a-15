package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.paw.webapp.form.AppointmentForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;

public class TakenAppointmentValidator implements ConstraintValidator<TakenAppointment, AppointmentForm>{

    private final AppointmentService as;

    @Autowired
    public TakenAppointmentValidator(AppointmentService as) {
        this.as = as;
    }

    @Override
    public boolean isValid(AppointmentForm form, ConstraintValidatorContext context) {
        return as.getAppointmentByShiftIdDateAndTime(form.getShiftId(), form.getDate(), form.getStartTime(), form.getEndTime()) == null;
    }
}