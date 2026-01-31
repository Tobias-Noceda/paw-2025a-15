package ar.edu.itba.paw.webapp.controller.util;

import java.security.Principal;
import java.util.function.Function;

import ar.edu.itba.paw.models.entities.User;

public class AuthenticatedUser {
    public static User get(Principal principal, Function<String, User> userFetcher) {
        if (principal == null || principal.getName() == null) {
            return null;
        }

        return userFetcher.apply(principal.getName());
    }
}
