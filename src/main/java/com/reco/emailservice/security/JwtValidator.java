package com.reco.emailservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtValidator {

    private final JwtSettings jwtSettings;
    private final Key userKey;
    private final Key serviceKey;

    public JwtValidator(JwtSettings jwtSettings) {
        this.jwtSettings = jwtSettings;

        this.userKey = Keys.hmacShaKeyFor(jwtSettings.getUser().getSecret().getBytes());
        this.serviceKey = Keys.hmacShaKeyFor(jwtSettings.getService().getSecret().getBytes());
    }

    /** Validate User JWT and return claims */
    public Claims validateUserToken(String token) throws JwtException {
        return validateToken(
                token,
                userKey,
                jwtSettings.getUser().getIssuer(),
                jwtSettings.getUser().getAudience(),
                jwtSettings.getUser().getClockSkewSeconds());
    }

    /** Validate Service JWT and return claims */
    public Claims validateServiceToken(String token) throws JwtException {
        return validateToken(
                token,
                serviceKey,
                jwtSettings.getService().getIssuer(),
                jwtSettings.getService().getAudience(),
                jwtSettings.getService().getClockSkewSeconds());
    }

    /** Core validation logic */
    private Claims validateToken(String token, Key key, String expectedIssuer, String expectedAudience,
            int clockSkewSeconds) throws JwtException {
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .setSigningKey(key)
                .requireIssuer(expectedIssuer)
                .requireAudience(expectedAudience)
                .build()
                .parseClaimsJws(token);

        Claims claims = jwsClaims.getBody();

        // Expiration check
        Date now = new Date();
        if (claims.getExpiration().before(now)) {
            throw new ExpiredJwtException(jwsClaims.getHeader(), claims, "Token expired");
        }

        return claims;
    }
}
