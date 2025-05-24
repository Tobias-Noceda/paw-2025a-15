package ar.edu.itba.paw.webapp.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import ar.edu.itba.paw.models.entities.User;

public class PawAuthUserDetails extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;
    private final User user;

    public PawAuthUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);
        this.user = user;
    }

    public PawAuthUserDetails(User user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
