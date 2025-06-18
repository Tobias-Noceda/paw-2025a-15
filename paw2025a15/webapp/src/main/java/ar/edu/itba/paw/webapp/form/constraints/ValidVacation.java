package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Definir la anotación personalizada
@Constraint(validatedBy = VacationValidator.class) // Asocia un validador con la anotación
@Target(ElementType.TYPE) // Aplica a clases
@Retention(RetentionPolicy.RUNTIME) // Disponible en tiempo de ejecución
public @interface ValidVacation {

    String message() default "La fecha de fin debe ser posterior a la fecha de inicio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}