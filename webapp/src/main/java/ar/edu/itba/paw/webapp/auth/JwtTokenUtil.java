package ar.edu.itba.paw.webapp.auth;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;

/**
 * "Inspired" by:
 *
 * @see https://github.com/jwtk/jjwt?tab=readme-ov-file#jwt-signed-with-hmac
 * @see https://github.com/JuArce/PAW_PopCult/blob/master/webapp/src/main/java/ar/edu/itba/paw/webapp/auth/JwtTokenUtil.java
 */
@Component
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Autowired
    private UserDetailsService userDetailsService;

    private final SecretKey key;
    private final UriBuilder base;

    private static final MacAlgorithm alg = Jwts.SIG.HS512;

    private static final long ACCESS_EXPIRY_TIME = TimeUnit.MINUTES.toMillis(5);
    private static final long REFRESH_EXPIRY_TIME = TimeUnit.DAYS.toMillis(30);

    public JwtTokenUtil(final String token, final UriBuilder base) throws IOException {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(token));
        this.base = base;
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
            return new SessionInfo((PawAuthUserDetails) userDetailsService.loadUserByUsername(content.getSubject()), content.get("role") != null, content.get("verify") != null, expired);
        } catch (UsernameNotFoundException e) {
            return null;
        }
    }

    public Session create(final User user) {
        LOGGER.debug("Creating token for user: {}", user.getEmail());
        
        final String refresh = createMailToken(user.getEmail(), REFRESH_EXPIRY_TIME);

        final String access = createAccessToken(user);

        LOGGER.debug("Created tokens: {} {}", access, refresh);
        return new Session(access, refresh);
    }

    public String createAccessToken(final User user) {
        LOGGER.debug("Creating access token for user: {}", user.getEmail());

        final String self = user.getRole().equals(UserRoleEnum.DOCTOR)
                ? base.clone()
                        .path("doctors")
                        .path(String.valueOf(user.getId()))
                        .build()
                        .toString()
                : base.clone()
                        .path("patients")
                        .path(String.valueOf(user.getId()))
                        .build()
                        .toString();

        final String access = Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("language", user.getLocale())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .claim("self", self)
                .claim("image", base.clone()
                        .path("files")
                        .path(String.valueOf(user.getPicture().getId()))
                        .build()
                        .toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRY_TIME))
                .signWith(key, alg).compact();

        LOGGER.debug("Created access token: {}", access);
        return access;
    }

    public String createMailToken(final String email, final long expiryTime) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(key, alg).compact();
    }

    public String createVerifyToken(final String email, final long expiryTime) {
        return Jwts.builder()
                .subject(email)
                .claim("verify", true)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(key, alg).compact();
    }

    public record SessionInfo(PawAuthUserDetails user, boolean isAccess, boolean isVerify, boolean expired) {
    };

    public record Session(String access, String refresh) {
    };
}
