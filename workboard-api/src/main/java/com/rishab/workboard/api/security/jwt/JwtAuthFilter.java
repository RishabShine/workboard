package com.rishab.workboard.api.security.jwt;

import com.rishab.workboard.api.security.auth.AuthUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // looking for Authorization header
        String header = req.getHeader("Authorization");

        // if no header or not Bearer -> we do nothing and continue.
        // SecurityConfig will later block protected endpoints if not authenticated.
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        // extract token string
        String token = header.substring(7);

        // validate token signature + expiry
        if (!jwtService.isValid(token)) {
            chain.doFilter(req, res);
            return;
        }

        // if request is not already authenticated, authenticate it using JWT claims
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = jwtService.parseClaims(token);

            // uid claim is the user id
            Long userId = claims.get("uid", Long.class);
            String username = claims.getSubject();

            // principal is what @AuthenticationPrincipal will receive
            AuthUser principal = new AuthUser(userId, username);

            // no roles/authorities for now (empty list)
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, List.of());

            // attach request info (ip, etc.)
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

            // store authentication for the rest of the request lifecycle
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // continue filter chain
        chain.doFilter(req, res);
    }
}
