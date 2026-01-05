package com.reco.emailservice.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtSettings {

    private TokenSettings user;
    private TokenSettings service;

    public TokenSettings getUser() {
        return user;
    }

    public void setUser(TokenSettings user) {
        this.user = user;
    }

    public TokenSettings getService() {
        return service;
    }

    public void setService(TokenSettings service) {
        this.service = service;
    }

    public static class TokenSettings {
        private String secret;
        private String issuer;
        private String audience;
        private int clockSkewSeconds;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public int getClockSkewSeconds() {
            return clockSkewSeconds;
        }

        public void setClockSkewSeconds(int clockSkewSeconds) {
            this.clockSkewSeconds = clockSkewSeconds;
        }
    }
}