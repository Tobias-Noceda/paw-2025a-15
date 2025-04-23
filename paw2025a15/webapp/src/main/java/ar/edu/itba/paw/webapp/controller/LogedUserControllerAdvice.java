package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

@ControllerAdvice
public class LogedUserControllerAdvice {
    private final UserService us;

    @Autowired
    public LogedUserControllerAdvice(final UserService us) {
        this.us = us;
    }

    @ModelAttribute(name = "user_data", binding = false)
    public User getLoggedUser() {
        return us.getCurrentUser().orElse(null);
    }
}
