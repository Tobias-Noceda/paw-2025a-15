package ar.edu.itba.paw.webapp.form.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Constraint(validatedBy= {PastDateValidator.class})
public @interface PastDate {
    String message() default "{pastDate.errorMessage}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
