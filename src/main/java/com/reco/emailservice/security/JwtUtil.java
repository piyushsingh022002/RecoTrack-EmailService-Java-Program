package com.reco.emailservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private final Key key;

    public JwtUtil() {
        String secret = "8787uybdf7bf-f-=-0923$5#@#5uaf8y23y4t8y23t4y23t4y23t4y23";
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        System.out.println(key.getEncoded().length);
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

}
