package ar.edu.itba.paw.webapp.form.constraints;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.paw.webapp.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;

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
        if (!as.getAppointmentByShiftIdDateAndTime(form.getShiftId(), form.getDate(), form.getStartTime(), form.getEndTime()).isEmpty()) {
            return false;
        }
        Optional<DoctorSingleShift> maybeShift = dss.getShiftById(form.getShiftId());
        if (maybeShift.isEmpty()) {
            return false;
        }
        DoctorSingleShift shift = maybeShift.get();
        return form.getDoctorId().equals(shift.getDoctor().getId()) && 
               shift.isValidStartAndEndTime(form.getStartTime(), form.getEndTime()) &&
               shift.isValidDate(form.getDate());
    }
}