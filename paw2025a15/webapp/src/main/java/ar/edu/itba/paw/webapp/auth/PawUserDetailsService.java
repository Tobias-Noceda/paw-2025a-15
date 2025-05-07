package ar.edu.itba.paw.webapp.auth;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;


@Component
public class PawUserDetailsService implements UserDetailsService {

    private final UserService us;
    
    @Autowired
    public PawUserDetailsService(final UserService us) {
        this.us = us;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = us.getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No user for email " + email));

        final Collection<GrantedAuthority> authorities = new HashSet<>();
        switch (user.getRole()) {
            case ADMIN -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            case DOCTOR -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_DOCTOR"));
            }
            case LABORATORY -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_LABORATORY"));
            }
            case PATIENT -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_PATIENT"));
            }
        }

        us.updateLocale(user.getId(), LocaleEnum.fromLocale(LocaleContextHolder.getLocale()));

        return new PawAuthUserDetails(user, authorities);
    }
}
