package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;


@Component
public class PawUserDetailsService implements UserDetailsService {

    private final UserService us;

    private final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[.0-9A-Za-z]{53}");

    @Autowired
    public PawUserDetailsService(final UserService us) {
        this.us = us;
    }


    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = us.getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No user for email " + email));
        if(!BCRYPT_PATTERN.matcher(user.getPassword()).matches()){
            // TODO: pedirle al usuario que actualice contra, por mail y hashearle la contra
            us.changePassword(email, user.getPassword());
            return loadUserByUsername(email);
        }
        //System.out.println("superado?  " + user.getEmail() + " " + user.getPassword());

        //TODO:Implement logic to grant only required authorities 
        final Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_DOCTOR"));
        authorities.add(new SimpleGrantedAuthority("ROLE_PATIENT "));

        return new PawAuthUserDetails(email,user.getPassword(),authorities);
    }
}
