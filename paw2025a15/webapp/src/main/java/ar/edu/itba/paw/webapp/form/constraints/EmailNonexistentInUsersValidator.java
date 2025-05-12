package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.paw.interfaces.services.UserService;

public class EmailNonexistentInUsersValidator implements ConstraintValidator<EmailNonexistentInUsers, Object> {
    private final UserService us;

    @Autowired
    public EmailNonexistentInUsersValidator(UserService us){
        this.us = us;
    }

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		return !(us.getUserByEmail(value.toString()).isPresent());
    }
}