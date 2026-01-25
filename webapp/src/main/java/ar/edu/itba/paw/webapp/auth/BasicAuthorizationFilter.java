package ar.edu.itba.paw.webapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.security.core.userdetails.UserDetailsService;

@Slf4j
@Component
public class BasicAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private AuthenticationEntryPoint aep;

    @Autowired
    private UserDetailsService uds;

    @Autowired
    private JwtTokenUtil jwt;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Basic ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String[] credentials = extractAndDecodeHeader(header);

            final PawAuthUserDetails user = (PawAuthUserDetails) uds.loadUserByUsername(credentials[0]);

            if (user == null || !user.isAccountNonLocked() || SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(request, response);
                return;
            }

            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword(), user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            JwtTokenUtil.Session session = jwt.create(user.getUser());

            response.setHeader("X-Access-Token", session.access());
            response.setHeader("X-Refresh-Token", session.refresh());
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            aep.commence(request, response, failed);
            return;
        }

        chain.doFilter(request, response);
    }

    private String[] extractAndDecodeHeader(String header) {
        final byte[] base64Token = header.split(" ")[1].trim().getBytes(StandardCharsets.UTF_8);

        final String token;
        try {
            token = new String(Base64.getDecoder().decode(base64Token), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        final String[] values = token.split(":");

        if (values.length != 2) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }

        return values;
    }
}
