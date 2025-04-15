package ar.edu.itba.paw.form.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Constraint(validatedBy= {ValidTurnStartValidator.class})
public @interface ValidTurnStart {
    String message() default "El turno ya no esta disponible";//TODO internacionalizacion

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
