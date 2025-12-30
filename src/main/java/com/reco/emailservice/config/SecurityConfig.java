package com.reco.emailservice.config;

import com.reco.emailservice.security.AuthProperties;
import com.reco.emailservice.security.filter.AuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

        @Bean
        public AuthFilter authFilter(AuthProperties authProperties) {
                return new AuthFilter(authProperties);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthFilter authFilter) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                                "/health",
                                                "/swagger",
                                                "/swagger/**",
                                                "/swagger-ui.html",
                                                "/swagger-ui/**",
                                                "/swagger-resources/**",
                                                "/v3/api-docs/**",
                                                "/webjars/**").permitAll()
                                                .anyRequest().authenticated())

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((req, res, e) -> res
                                                                .sendError(HttpServletResponse.SC_UNAUTHORIZED)))

                                .addFilterBefore(
                                                authFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .httpBasic(Customizer.withDefaults())
                                .formLogin(form -> form.disable());

                return http.build();
        }
}
