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

import ar.edu.itba.paw.interfaces.services.UserService;

@Component
public class BearerAuthorizationFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationFilter.class);

    @Autowired
    private UserService us;

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
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final JwtTokenUtil.SessionInfo info = jwt.parse(token);
        LOGGER.debug("Got user details: {}", info);

        if (info == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            LOGGER.debug("Invalid user details");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!info.user().isEnabled()) {
            if (info.isVerify() && !info.expired()) {
                LOGGER.info("Verifying user with token: {}", token);
                System.out.println("Verifying user with mail: " + info.user().getUser().getEmail());
                us.verifyUser(info.user().getUser().getEmail());
                chain.doFilter(request, response);
                return;
            } else {
                LOGGER.info("User not active and token is not valid for verification: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        if (info.expired()) {
            LOGGER.debug("Session expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!info.isAccess()) {
            LOGGER.debug("Session token used, creating new refresh token");

            response.setHeader("X-Access-Token", jwt.createAccessToken(((PawAuthUserDetails) info.user()).getUser()));
        }

        LOGGER.debug("Setting authentication");
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            info.user().getUser(),
            info.user().getPassword(),
            info.user().getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // response.setHeader(HttpHeaders.AUTHORIZATION, header);
        chain.doFilter(request, response);
    }
}
