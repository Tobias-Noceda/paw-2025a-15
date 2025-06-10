package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidLicenceValidator implements ConstraintValidator<ValidLicence, Object>{
    private final DoctorService ds;
    @Autowired
    public ValidLicenceValidator(DoctorService ds){
        this.ds = ds;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return !ds.licenceExists(value.toString());
    }
}
