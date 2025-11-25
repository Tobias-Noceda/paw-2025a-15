package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.webapp.form.LandingForm;

public class LandingFormValidator implements ConstraintValidator<IsValidLandingForm, LandingForm>{

    @Override
    public boolean isValid(LandingForm form, ConstraintValidatorContext context){
        return true;
    }
}