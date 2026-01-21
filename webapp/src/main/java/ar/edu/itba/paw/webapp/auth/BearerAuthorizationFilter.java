package ar.edu.itba.paw.webapp.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BearerAuthorizationFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationFilter.class);

    @Autowired
    private JwtTokenUtil jwt;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        LOGGER.debug("Got header: {}", header);

        if (header == null || !header.startsWith("Bearer ")) {
            LOGGER.debug("No bearer token found");
            chain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();
        LOGGER.debug("Got token: {}", token);

        if (token.isEmpty()) {
            LOGGER.debug("Empty token");
            chain.doFilter(request, response);
            return;
        }

        final JwtTokenUtil.SessionInfo info = jwt.parse(token);
        LOGGER.debug("Got user details: {}", info);

        if (info == null || !info.user().isAccountNonLocked()
                || SecurityContextHolder.getContext().getAuthentication() != null) {
            LOGGER.debug("Invalid user details");
            chain.doFilter(request, response);
            return;
        }

        if (info.expired()) {
            LOGGER.debug("Session expired");

            final String refreshHeader = request.getHeader("X-Refresh-Token");
            LOGGER.debug("Got refresh header: {}", refreshHeader);

            if (refreshHeader == null || !refreshHeader.startsWith("Bearer ")) {
                LOGGER.debug("No refresh token found");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            final String refreshToken = refreshHeader.split(" ")[1].trim();
            LOGGER.debug("Got refresh token: {}", refreshToken);

            if (refreshToken.isEmpty()) {
                LOGGER.debug("Empty refresh token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            final JwtTokenUtil.SessionInfo refreshParsed = jwt.parse(refreshToken);
            if (refreshParsed == null || refreshParsed.expired()) {
                LOGGER.debug("Invalid or expired refresh token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!refreshParsed.user().equals(info.user())) {
                LOGGER.debug("Refresh token user does not match session user");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            LOGGER.debug("Accepted refresh token");
        }

        LOGGER.debug("Setting authentication");
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                info.user().getUsername(),
                info.user().getPassword(),
                info.user().getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // response.setHeader(HttpHeaders.AUTHORIZATION, header);
        chain.doFilter(request, response);
    }
}
