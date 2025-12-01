package ar.edu.itba.paw.webapp.config.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class StaticCacheFilter extends OncePerRequestFilter {
    public static final int MAX_TIME = 31536000;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", String.format("public, max-age=%d, inmutable", MAX_TIME));
        chain.doFilter(request, response);
    }
}
