package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.webapp.form.ProfileForm;

public class InsuranceNumberValidator implements ConstraintValidator<ValidInsuranceNumber, ProfileForm>{

    @Override
    public boolean isValid(ProfileForm form, ConstraintValidatorContext context){
        if (form.getInsuranceId() == null) {
            return true;
        }

        return form.getInsuranceNumber().length() >= 8;
    }
}
