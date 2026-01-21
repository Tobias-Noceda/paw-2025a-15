package ar.edu.itba.paw.webapp.auth;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * "Inspired" by:
 *
 * @see https://github.com/jwtk/jjwt?tab=readme-ov-file#jwt-signed-with-hmac
 * @see https://github.com/JuArce/PAW_PopCult/blob/master/webapp/src/main/java/ar/edu/itba/paw/webapp/auth/JwtTokenUtil.java
 */
@Slf4j
@Component
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Autowired
    private UserDetailsService userDetailsService;

    private final SecretKey key;

    private static final MacAlgorithm alg = Jwts.SIG.HS512;

    private static final long SESSION_EXPIRY_TIME = TimeUnit.MINUTES.toMillis(5);
    private static final long REFRESH_EXPIRY_TIME = TimeUnit.DAYS.toMillis(30);

    public JwtTokenUtil(final String token) throws IOException {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(token));
    }

    public SessionInfo parse(final String jws) {
        Claims content;
        boolean expired = false;
        try {
            content = Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jws)
                    .getPayload();

            LOGGER.debug("Got user: {}", content.getSubject());
        } catch (ExpiredJwtException e) {
            LOGGER.debug("Wow, that's an old token!");
            expired = true;

            content = e.getClaims();
            LOGGER.debug("Got user: {}", content.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.debug("Invalid token", e);
            return null;
        }

        try {
            return new SessionInfo(userDetailsService.loadUserByUsername(content.getSubject()), expired);
        } catch (UsernameNotFoundException e) {
            return null;
        }
    }

    public Session create(final User user, final UriBuilder baseUriBuilder) {
        LOGGER.debug("Creating token for user: {}", user.getEmail());
        final String session = Jwts.builder()
                .subject(user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .claim("image", baseUriBuilder
                        .clone()
                        .path("files")
                        .path(String.valueOf(user.getPicture().getId()))
                        .build()
                        .toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + SESSION_EXPIRY_TIME))
                .signWith(key, alg).compact();

        final String refresh = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRY_TIME))
                .signWith(key, alg).compact();

        LOGGER.debug("Created token: {} {}", session, refresh);
        return new Session(session, refresh);
    }

    public record SessionInfo(UserDetails user, boolean expired) {
    };

    public record Session(String session, String refresh) {
    };
}
