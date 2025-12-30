package com.reco.emailservice.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import com.reco.emailservice.security.jwt.JwtValidator;
import com.reco.emailservice.security.hmac.HmacValidator;
import com.reco.emailservice.security.client.ClientValidator;
import com.reco.emailservice.security.AuthProperties;

import java.io.IOException;

public class AuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator =
        new JwtValidator(
            "my-super-secret-key-change-later",
            "reco-email-service",
            "reco-clients"
        );

    private final HmacValidator hmacValidator =
        new HmacValidator("reco-client-secret");

    private final ClientValidator clientValidator =
        new ClientValidator(Set.of("reco-web", "reco-admin"));

    @Autowired
    private AuthProperties authProperties;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Extract headers
        String authHeader = request.getHeader("Authorization");
        String clientId = request.getHeader("X-CLIENT-ID");
        String signature = request.getHeader("X-SIGNATURE");

        if (authProperties.getDev().isEnabled() &&
            ("Bearer " + authProperties.getDev().getToken()).equals(authHeader)) {

            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        jwtValidator.validate(token);

        try {
            clientValidator.validate(clientId);
        } catch (IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // We will add validations step by step
        filterChain.doFilter(request, response);
    }
}
