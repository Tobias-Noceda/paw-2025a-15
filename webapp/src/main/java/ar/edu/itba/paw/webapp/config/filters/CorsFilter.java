package ar.edu.itba.paw.webapp.config.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @see https://stackoverflow.com/questions/28065963/how-to-handle-cors-using-jax-rs-with-jersey
 */
public class CorsFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {
        final String origin = request.getHeader("Origin");

        if (origin == null || !origin.equalsIgnoreCase("http://localhost:5173")) {
            chain.doFilter(request, response);
            return;
        }

        final String headers = "X-Requested-With, X-Auth-Token, X-Refresh-Token, Link, Location, Authorization, Accept-Version, Content-MD5, CSRF-Token, Content-Type, Content-Length, X-Current-Page, X-Total-Pages, X-Current-Date, X-Max-Date";

        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD");
        response.addHeader("Access-Control-Allow-Headers", headers);
        response.addHeader("Access-Control-Expose-Headers", headers);
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:5173");

        if (isPreflightRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * A preflight request is an OPTIONS request
     * with an Origin header.
     */
    private static boolean isPreflightRequest(final HttpServletRequest request) {
        return request.getHeader("Origin") != null && request.getMethod().equalsIgnoreCase("OPTIONS");
    }
}
