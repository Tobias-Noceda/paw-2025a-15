package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.webapp.form.TakeTurnForm;

public class AvailableTurnValidator implements ConstraintValidator<AvailableTurn, TakeTurnForm>{

    private final AppointmentService as;

    private final DoctorShiftService dss;

    @Autowired
    public AvailableTurnValidator(AppointmentService as, DoctorShiftService dss) {
        this.as = as;
        this.dss = dss;
    }

    @Override
    public boolean isValid(TakeTurnForm form, ConstraintValidatorContext context){
        if (as.getAppointmentByShiftIdDateAndTime(form.getShiftId(), form.getDate(), form.getStartTime(), form.getEndTime()) != null) {
            return false;
        }
        DoctorSingleShift shift = dss.getShiftById(form.getShiftId()).orElse(null);
        if (shift == null) {
            return false;
        }

        return form.getDoctorId().equals(shift.getDoctor().getId()) && 
               shift.isValidStartAndEndTime(form.getStartTime(), form.getEndTime()) &&
               shift.isValidDate(form.getDate());
    }
}