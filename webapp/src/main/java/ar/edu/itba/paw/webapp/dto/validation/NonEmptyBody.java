package ar.edu.itba.paw.webapp.dto.validation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import ar.edu.itba.paw.webapp.dto.validation.constraint.NonEmptyBodyConstraint;

@Documented
@Constraint(validatedBy = NonEmptyBodyConstraint.class)
@Target({ TYPE, FIELD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface NonEmptyBody {

    String message() default "{custom.validation.NotEmptyBody.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}