package com.reco.emailservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter validates both:
 * - User JWT: Authorization: Bearer <token>
 * - Service JWT: X-Service-Authorization: <token>
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    public JwtAuthenticationFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException {

        try {
            // 1️⃣ Validate Service JWT first (required for all calls)
            String serviceToken = request.getHeader("X-Service-Authorization");
            if (serviceToken == null || serviceToken.isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Service JWT");
                return;
            }

            Claims serviceClaims;
            try {
                serviceClaims = jwtValidator.validateServiceToken(serviceToken);
            } catch (ExpiredJwtException ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service token expired");
                return;
            } catch (JwtException ex) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Service token");
                return;
            }

            // Optionally: cache serviceClaims or log minimal info (never raw token)

            // 2️⃣ Validate User JWT if present
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String userToken = authHeader.substring(7);

                Claims userClaims;
                try {
                    userClaims = jwtValidator.validateUserToken(userToken);
                } catch (ExpiredJwtException ex) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User token expired");
                    return;
                } catch (JwtException ex) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid User token");
                    return;
                }

                // Map claims to Authentication
                String userId = userClaims.get("nameid", String.class);
                String username = userClaims.get("name", String.class);
                String email = userClaims.get("email", String.class);

                var authorities = List.of(new SimpleGrantedAuthority("SCOPE_EMAIL_SEND"));
                var authentication = new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(userId, username, email),
                        null,
                        authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // Fallback: 500 internal for unexpected errors
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error");
        }
    }
}