package com.reco.emailservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

public class JwtSelfTest {

    public static void main(String[] args) {

        String secret = "8787uybdf7bf-f-=-0923$5#@#5uaf8y23y4t8y23t4y23t4y23t4y23";

        var key = Keys.hmacShaKeyFor(secret.getBytes());

        String token = Jwts.builder()
                .setSubject("backend-service")
                .claim("scope", "EMAIL_SEND")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(key)
                .compact();

        System.out.println("JAVA TOKEN:");
        System.out.println(token);

        // 🔎 validate immediately
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        System.out.println("JAVA TOKEN VALIDATED SUCCESSFULLY");
    }
}
