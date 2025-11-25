package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
@Constraint(validatedBy = { ValidLicenceValidator.class })
public @interface ValidLicence {
    String message() default "{error.licenceExists}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
