package ar.edu.itba.paw.webapp.dto.validation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;

public class NonEmptyBodyConstraint implements ConstraintValidator<NonEmptyBody, Object> {

    public void initialize(NonEmptyBody constraint) {
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext ctx) {
        return object != null;
    }
}
