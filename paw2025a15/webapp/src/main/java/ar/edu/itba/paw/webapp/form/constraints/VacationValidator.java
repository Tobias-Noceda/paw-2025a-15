package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.webapp.form.VacationForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class VacationValidator implements ConstraintValidator<ValidVacation, VacationForm> {
    @Autowired
    private DoctorService ds;
        
    @Override
    public boolean isValid(VacationForm form, ConstraintValidatorContext context) {
        if(form.isCanceling()) return true;

        if (form.getStartDate() == null || form.getEndDate() == null) {
            return false;
        }
        return form.getEndDate().isAfter(form.getStartDate()) && !ds.vacationExists(form.getDoctorId(), form.getStartDate(), form.getEndDate());
    }
}