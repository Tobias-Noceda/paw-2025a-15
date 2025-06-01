package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidLicenceValidator implements ConstraintValidator<ValidLicence, Object>{
    private final DoctorDetailService dds;
    @Autowired
    public ValidLicenceValidator(DoctorDetailService dds){
        this.dds = dds;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return !dds.licenceExists(value.toString());
    }
}
