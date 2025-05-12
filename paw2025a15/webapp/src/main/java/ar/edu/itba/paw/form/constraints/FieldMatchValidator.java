package ar.edu.itba.paw.form.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object>{

    private String firstField;
    private String secondField;
    private String message;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstField=constraintAnnotation.first();
        secondField=constraintAnnotation.second();
        message=constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        try
        {
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);

            final Object firstObj = wrapper.getPropertyValue(firstField);
            final Object secondObj = wrapper.getPropertyValue(secondField);
            boolean valid = (firstObj == null && secondObj == null) || firstObj != null && firstObj.equals(secondObj);

            if (!valid) {
                context.disableDefaultConstraintViolation();

                context.buildConstraintViolationWithTemplate(message)
                       .addPropertyNode(secondField)
                       .addConstraintViolation();
            }

            return valid;
        }
        catch (final Exception ignore)
        {
            return false;
        }
    }
    
}
