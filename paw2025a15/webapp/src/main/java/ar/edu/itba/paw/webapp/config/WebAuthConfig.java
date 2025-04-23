package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.CustomAuthenticationFailureHandler;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

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
        http.sessionManagement()
                .invalidSessionUrl("/home")
                .and().authorizeRequests()
                .antMatchers("/login").anonymous()
                .antMatchers("/register/**").anonymous()
                .antMatchers("/forgot-password").anonymous()
                .antMatchers("/patient/**").hasRole("DOCTOR")
                .antMatchers("/patient/**").hasRole("LABORATORY")
                .antMatchers("/search/**").permitAll()
                .antMatchers("/changePassword/**").anonymous()
                .antMatchers("/recover-password").anonymous()
                .antMatchers("/", "/home").permitAll()
                .antMatchers("/supersecret/files/**").permitAll()
                .antMatchers("/supersecret/file/icon").permitAll()
                .antMatchers("/appointments").hasRole("DOCTOR")
                .antMatchers("/appointments").hasRole("PATIENT")
                .antMatchers("/patient/upload/**").permitAll()
                .antMatchers("/createPatient").anonymous()
                .antMatchers("/createMedic").anonymous()
                .antMatchers("/createLab").anonymous()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/doctor/**").hasRole("DOCTOR")
                .antMatchers("/**").authenticated()
                .and().formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false)
                .loginPage("/login")
                .failureHandler(authenticationFailureHandler)
                .and().rememberMe()
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService)
                .key("mysupersecretketthatnobodyknowsabout")
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .and().exceptionHandling()
                .accessDeniedPage("/403")
                .and().csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/403", "/resources/**", "/supersecret/files/**");
    }
}