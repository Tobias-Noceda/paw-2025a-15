package ar.edu.itba.paw.webapp.controller.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ar.edu.itba.paw.models.entities.User;

public class AuthenticatedUser {
    public static User get() {
        Authentication session = SecurityContextHolder.getContext().getAuthentication();
        if (session != null && session.getPrincipal() instanceof User user) {
            return user;
        }
        
        return null;
    }
}
