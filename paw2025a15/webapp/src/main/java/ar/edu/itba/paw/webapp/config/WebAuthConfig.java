package ar.edu.itba.paw.webapp.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.itba.paw.webapp.auth.CustomAuthenticationFailureHandler;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;

@Configuration
@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement(management -> management.invalidSessionUrl("/home"))
            .authorizeRequests(requests -> requests
                .antMatchers("/login").anonymous()
                .antMatchers("/register/**").anonymous()
                .antMatchers("/forgot-password").anonymous()
                .antMatchers("/patient/**").hasAnyRole("DOCTOR", "LABORATORY")
                .antMatchers("/search/**").permitAll()
                .antMatchers("/changePassword/**").anonymous()
                .antMatchers("/recover-password").anonymous()
                .antMatchers("/", "/home").permitAll()
                .antMatchers("/supersecret/files/**").permitAll()
                .antMatchers("/supersecret/file/icon").permitAll()
                .antMatchers("/appointments").hasAnyRole("DOCTOR", "PATIENT")
                .antMatchers("/upload-file/**").authenticated()
                .antMatchers("/takeAppointment").hasRole("PATIENT")
                .antMatchers("/createPatient").anonymous()
                .antMatchers("/patientCancelAppointment").hasRole("PATIENT")
                .antMatchers("/createMedic").anonymous()
                .antMatchers("/createLab").anonymous()
                .antMatchers("/doctors/**").authenticated()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/doctor/**").hasRole("DOCTOR")
                .antMatchers("/**").permitAll()
            )
            .formLogin(login -> login
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false)
                .loginPage("/login")
                .failureHandler(authenticationFailureHandler)
            )
            .rememberMe(me -> me
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService)
                .key("mysupersecretketthatnobodyknowsabout")
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            )
            .exceptionHandling(handling -> handling.accessDeniedPage("/403"))
            .csrf(csrf -> csrf.disable());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
