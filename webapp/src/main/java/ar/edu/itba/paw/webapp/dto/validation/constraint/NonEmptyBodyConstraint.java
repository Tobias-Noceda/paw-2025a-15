package ar.edu.itba.paw.webapp.dto.validation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;

public class NonEmptyBodyConstraint implements ConstraintValidator<NonEmptyBody, Object> {

    public void initialize(NonEmptyBody constraint) {
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext ctx) {
        
        if (object == null) return false;

        for (var field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(object) != null) {
                    return true; // at least one not null
                }
            } catch (IllegalAccessException e) {
                // ignore
            }
        }

        return false;
    }
}
