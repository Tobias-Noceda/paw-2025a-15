package ar.edu.itba.paw.webapp.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.webapp.auth.BasicAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.BearerAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.auth.WebUserAuthDecision;

@Configuration
@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@PropertySource("classpath:application.properties")
@SuppressWarnings("deprecation")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Autowired
    private WebUserAuthDecision ad;

    @Autowired
    private Environment env;

    @Autowired
    private BasicAuthorizationFilter basic;

    @Autowired
    private BearerAuthorizationFilter bearer;

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

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new UnauthorizedRequestHandler();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() throws IOException {
        final String token = env.getProperty("security.key");
        String baseUrl = env.getProperty("api.base.url", "http://pawserver.it.itba.edu.ar/paw-2025a-15/api");

        if (token == null) {
            throw new IOException("Missing JWT secret key in configuration. Please set 'security.key' property.");
        }

        return new JwtTokenUtil(token, UriBuilder.fromUri(baseUrl));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                // general
                // appointments
                .requestMatchers(HttpMethod.GET, "/api/appointments")
                    .access((a, c) -> ad.canAccessAppointments(a.get(), c))
                .requestMatchers(HttpMethod.GET, "/api/appointments/{id}")
                    .access((a, c) -> ad.canModifyAppointment(a.get(),  c.getVariables().get("id")))
                .requestMatchers(HttpMethod.PATCH, "/api/appointments/{id}")
                    .access((a, c) -> ad.canModifyAppointment(a.get(), c.getVariables().get("id")))

                // doctors
                .requestMatchers(HttpMethod.GET, "/api/doctors/{id}/authorizations").hasRole("PATIENT")
                .requestMatchers(HttpMethod.PUT, "/api/doctors/{id}/authorizations").hasRole("PATIENT")
                .requestMatchers(HttpMethod.GET, "/api/doctors/{id}/shifts").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/doctors/{id}/shifts")
                    .access((a, c) -> ad.canModifyDoctorShifts(a.get(), c.getVariables().get("id")))
                .requestMatchers(HttpMethod.GET, "/api/doctors").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/doctors").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/doctors/{id}").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/doctors/{id}")
                    .access((a, c) -> ad.isSelfDecision(a.get(), Long.parseLong(c.getVariables().get("id"))))
                .requestMatchers(HttpMethod.GET, "/api/doctors/{id}/vacations").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/doctors/{id}/vacations")
                    .access((a, c) -> ad.isDoctorByParam(a.get(), c.getVariables().get("id") != null ? Long.valueOf(c.getVariables().get("id")) : null))
                .requestMatchers(HttpMethod.DELETE, "/api/doctors/{id}/vacations")
                    .access((a, c) -> ad.isDoctorByParam(a.get(), c.getVariables().get("id") != null ? Long.valueOf(c.getVariables().get("id")) : null))
                
                // files
                .requestMatchers(HttpMethod.GET, "/api/files")
                    .access((a, c) -> ad.hasStudyAuth(a.get(), null, c.getRequest().getParameter("studyId") != null ? Long.valueOf(c.getRequest().getParameter("studyId")) : null))
                .requestMatchers(HttpMethod.POST, "/api/files").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/files/{id}").permitAll()

                // insurances
                .requestMatchers(HttpMethod.GET, "/api/insurances").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/insurances").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/insurances/{id}").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/insurances/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/insurances/{id}").hasRole("ADMIN")

                // patients
                //// patient info
                .requestMatchers(HttpMethod.GET, "/api/patients/{id}/medicalInfo")
                    .access((a, c) -> ad.canSeePatientInfo(a.get(), Long.parseLong(c.getVariables().get("id")), AccessLevelEnum.VIEW_MEDICAL))
                .requestMatchers(HttpMethod.PATCH, "/api/patients/{id}/medicalInfo")
                    .access((a, c) -> ad.isSelfDecision(a.get(), Long.parseLong(c.getVariables().get("id"))))
                .requestMatchers(HttpMethod.GET, "/api/patients/{id}/socialInfo")
                    .access((a, c) -> ad.canSeePatientInfo(a.get(), Long.parseLong(c.getVariables().get("id")), AccessLevelEnum.VIEW_SOCIAL))
                .requestMatchers(HttpMethod.PATCH, "/api/patients/{id}/socialInfo")
                    .access((a, c) -> ad.isSelfDecision(a.get(), Long.parseLong(c.getVariables().get("id"))))
                .requestMatchers(HttpMethod.GET, "/api/patients/{id}/habitsInfo")
                    .access((a, c) -> ad.canSeePatientInfo(a.get(), Long.parseLong(c.getVariables().get("id")), AccessLevelEnum.VIEW_HABITS))
                .requestMatchers(HttpMethod.PATCH, "/api/patients/{id}/habitsInfo")
                    .access((a, c) -> ad.isSelfDecision(a.get(), Long.parseLong(c.getVariables().get("id"))))

                //// patient studies
                .requestMatchers(HttpMethod.GET, "/api/patients/{id}/studies")
                    .access((a, c) -> ad.isAuthDoctorByParamOrSelf(a.get(), Long.valueOf(c.getVariables().get("id")), c.getRequest().getParameter("doctorId") != null ? Long.valueOf(c.getRequest().getParameter("doctorId")) : null))
                .requestMatchers(HttpMethod.POST, "/api/patients/{id}/studies")
                    .access((a, c) -> ad.isAuthDoctorOrSelf(a.get(), Long.valueOf(c.getVariables().get("id"))))
                .requestMatchers(HttpMethod.GET, "/api/patients/{id}/studies/{studyId}")
                    .access((a, c) -> ad.hasStudyAuth(a.get(), Long.valueOf(c.getVariables().get("id")), Long.valueOf(c.getVariables().get("studyId"))))
                .requestMatchers(HttpMethod.DELETE, "/api/patients/{id}/studies/{studyId}")
                    .access((a, c) -> ad.isSelfDecision(a.get(), Long.parseLong(c.getVariables().get("id"))))
                .requestMatchers(HttpMethod.PATCH, "/api/patients/{id}/studies/{studyId}")
                    .access((a, c) -> ad.isSelfDecision(a.get(), Long.parseLong(c.getVariables().get("id"))))
                
                //// patient general
                .requestMatchers(HttpMethod.GET, "/api/patients/{id}")
                    .access((a, c) -> ad.isAuthDoctorOrSelf(a.get(), Long.valueOf(c.getVariables().get("id"))))
                .requestMatchers(HttpMethod.PATCH, "/api/patients/{id}")
                    .access((a, c) -> ad.isSelfDecision(a.get(), Long.parseLong(c.getVariables().get("id"))))
                .requestMatchers(HttpMethod.GET, "/api/patients")
                    .access((a, c) -> ad.isDoctorByParam(a.get(), c.getRequest().getParameter("doctorId") != null ? Long.valueOf(c.getRequest().getParameter("doctorId")) : null))
                .requestMatchers(HttpMethod.POST, "/api/patients").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/patients/{id}")
                    .access((a, c) -> ad.isAuthDoctorOrSelf(a.get(), Long.valueOf(c.getVariables().get("id"))))
                .requestMatchers(HttpMethod.PATCH, "/api/patients/{id}")
                    .access((a, c) -> ad.isSelfDecision(a.get(), Long.parseLong(c.getVariables().get("id"))))
            )
            .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new UnauthorizedRequestHandler())
                        .accessDeniedHandler(new ForbiddenRequestHandler()))
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .headers(h -> h.cacheControl().disable())
            .addFilterBefore(basic, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(bearer, UsernamePasswordAuthenticationFilter.class);
    }

    private class UnauthorizedRequestHandler implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
                throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().flush();
        }
    }

    private class ForbiddenRequestHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
                throws IOException {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().flush();
        }
    }
}
