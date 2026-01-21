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
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
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

        if (token == null) {
            throw new IOException("Missing JWT secret key in configuration. Please set 'security.key' property.");
        }

        return new JwtTokenUtil(token);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("application.properties"));
        
        return configurer;
    }
    
    @Bean
    public BasicAuthorizationFilter basicAuthorizationFilter() {
        // Get base URL from environment or use default
        String baseUrl = env.getProperty("api.base.url", "http://localhost:8080/paw-2025a-15/api");
        return new BasicAuthorizationFilter(UriBuilder.fromUri(baseUrl));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                // general
                // doctors
                .requestMatchers(HttpMethod.GET, "/api/doctors").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/doctors/**/shifts").permitAll()
            )
            .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new UnauthorizedRequestHandler())
                        .accessDeniedHandler(new ForbiddenRequestHandler()))
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .headers(h -> h.cacheControl().disable())
            .addFilterBefore(basicAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(bearer, UsernamePasswordAuthenticationFilter.class);
    }

    private class UnauthorizedRequestHandler implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
                throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private class ForbiddenRequestHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
                throws IOException {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
