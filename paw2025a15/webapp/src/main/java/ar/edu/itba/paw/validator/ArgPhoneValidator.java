package ar.edu.itba.paw.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.annotation.ValidArgPhone;
//TODO capaz puede ser constraint directo como las otras?
public class ArgPhoneValidator implements ConstraintValidator<ValidArgPhone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return false;

        String number = value.replaceAll("[\\s\\-()]", "");
        if (number.startsWith("+")) {
            number = number.replaceFirst("\\+", "00");
        }

        return number.matches("^00549\\d{10}$") ||       // +54 9 11 XXXXXXXX
               number.matches("^0054\\d{10}$") ||       // +54 11 XXXXXXXX
               number.matches("^0?11?15\\d{7}$") ||      // 01115XXXXXXX or 115XXXXXXX
               number.matches("^11\\d{8}$");             // 11XXXXXXXX
    }
}