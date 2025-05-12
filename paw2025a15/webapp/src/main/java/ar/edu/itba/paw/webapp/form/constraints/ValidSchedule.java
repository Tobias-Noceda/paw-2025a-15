package ar.edu.itba.paw.webapp.form.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = ValidScheduleValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSchedule {
    String message() default "{form.schedules.invalid}";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
