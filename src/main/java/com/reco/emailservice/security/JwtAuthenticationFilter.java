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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT Authentication Filter
 *
 * Validates authentication tokens from incoming requests:
 * - User JWT: Authorization: Bearer <token>
 * - Service Token: X-Service-Token: <token>
 *
 * Supports endpoints requiring:
 * - User JWT only
 * - Service Token only
 * - Both tokens (user JWT + service token)
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // Public endpoints that don't require authentication
    private static final String[] PUBLIC_ENDPOINTS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/health",
            "/health/status",
            "/api/email/verify/**"
    };

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
            // Extract tokens from headers
            String authHeader = request.getHeader("Authorization");
            String serviceToken = request.getHeader("X-Service-Token");

            boolean hasUserToken = authHeader != null && authHeader.startsWith("Bearer ");
            boolean hasServiceToken = serviceToken != null && !serviceToken.isBlank();

            UserPrincipal userPrincipal = null;
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            // Validate User JWT if present
            if (hasUserToken) {
                String userToken = authHeader.substring(7);
                try {
                    Claims userClaims = jwtValidator.validateUserToken(userToken);

                    String userId = userClaims.get(DotNetClaimNames.NAME_IDENTIFIER, String.class);
                    String username = userClaims.get(DotNetClaimNames.NAME, String.class);
                    String email = userClaims.get(DotNetClaimNames.EMAIL, String.class);

                    userPrincipal = new UserPrincipal(userId, username, email);
                    authorities.add(new SimpleGrantedAuthority("SCOPE_EMAIL_SEND"));

                } catch (ExpiredJwtException ex) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User token expired");
                    return;
                } catch (JwtException ex) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid User token");
                    return;
                }
            }

            // Validate Service Token if present
            if (hasServiceToken) {
                try {
                    jwtValidator.validateServiceToken(serviceToken);
                    authorities.add(new SimpleGrantedAuthority("SERVICE_EMAIL_SEND"));
                    authorities.add(new SimpleGrantedAuthority("SERVICE_EMAIL_CRITICAL"));

                } catch (ExpiredJwtException ex) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service token expired");
                    return;
                } catch (JwtException ex) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Service token");
                    return;
                }
            }

            // Set authentication if at least one token is valid
            if (userPrincipal != null || hasServiceToken) {
                // Use user principal if available, otherwise create a generic principal for
                // service-only calls
                Object principal = userPrincipal != null ? userPrincipal : "service-client";

                var authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // Fallback for unexpected errors
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Authentication error: " + ex.getMessage());
        }
    }

    /**
     * Skip filter for public endpoints that don't require authentication
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pattern : PUBLIC_ENDPOINTS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}