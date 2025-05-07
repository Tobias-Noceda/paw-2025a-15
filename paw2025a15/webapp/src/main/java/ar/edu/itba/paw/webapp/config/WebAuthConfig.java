package ar.edu.itba.paw.webapp.config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.auth.CustomAuthenticationFailureHandler;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.auth.WebUserAuthDecision;

@Configuration
@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@SuppressWarnings("deprecation")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private WebUserAuthDecision ad;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers("/css/**", "/js/**", "/resources/**", "/403");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement(management -> management.invalidSessionUrl("/home"))
            .authorizeHttpRequests(requests -> requests
                // general
                .requestMatchers("/", "/home").permitAll()
                .requestMatchers("/supersecret/files/logo").permitAll()
                .requestMatchers("/favicon").permitAll()
                .requestMatchers("/supersecret/user-profile-pic/{userId}").permitAll()
                .requestMatchers("/supersecret/insurance-picture/{userId}").permitAll()
                .requestMatchers("/save-profile").hasAnyRole("DOCTOR", "PATIENT")
                .requestMatchers("/doctors/{doctorId}", "/patientAuthDoctor/{doctorId}").hasRole("PATIENT")
                .requestMatchers("/patient/{patientId}")
                    .access((a, c) -> ad.isAuthDoctor(a.get(), Long.parseLong(c.getVariables().get("patientId"))))
                .requestMatchers("/login", "/register", "/forgot-password", "/changePassword/**", "/recover-password", "/createPatient", "/createMedic").anonymous()
                .requestMatchers("/403").permitAll()
                // appointments
                .requestMatchers("/appointments").hasAnyRole("DOCTOR", "PATIENT")
                .requestMatchers("/cancelAppointment").hasAnyRole("DOCTOR", "PATIENT")
                .requestMatchers("/takeAppointment").hasRole("PATIENT")
                .requestMatchers("/removeAppointment").hasRole("DOCTOR")
                // studies
                .requestMatchers("/studies").hasRole("PATIENT")
                .requestMatchers("/view-study/{studyId}")
                    .access((a, c) -> ad.hasStudyAuth(a.get(), Long.parseLong(c.getVariables().get("studyId"))))
                .requestMatchers("/upload-study/{patientId}") // TODO: change to /upload-study
                    .access((a, c) -> ad.isAuthDoctorOrSelf(a.get(), Long.parseLong(c.getVariables().get("patientId"))))
                // temporary
                .requestMatchers("/**").permitAll()
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
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(new DecisionExceptionHandler(), UsernamePasswordAuthenticationFilter.class);
    }

    private class DecisionExceptionHandler extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                throws IOException, ServletException {
            try {
                chain.doFilter(req, res);
            } catch (NotFoundException e) {
                req.getRequestDispatcher("/404").forward(req, res);
            }
        }
    }
}
