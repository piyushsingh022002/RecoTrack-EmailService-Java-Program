package com.reco.emailservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtSelfTest {

    public static void main(String[] args) {

        String secret = loadSecretFromConfig();

        var key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

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

    private static String loadSecretFromConfig() {
        try {
            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
            Resource resource = new ClassPathResource("application.yml");
            PropertySource<?> yaml = loader.load("application", resource).get(0);

            Map<String, Object> envVars = new HashMap<>(System.getenv());

            MutablePropertySources propertySources = new MutablePropertySources();
            propertySources.addFirst(new MapPropertySource("envVars", envVars));
            propertySources.addLast(yaml);

            PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);
            resolver.setIgnoreUnresolvableNestedPlaceholders(true);
            String secret = resolver.getProperty("jwt.secret");

            if (secret == null || secret.isBlank() || secret.contains("${")) {
                throw new IllegalStateException("jwt.secret could not be resolved. Ensure JWT_SECRET env var is set.");
            }

            return secret;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load jwt.secret from application.yml", e);
        }
    }
}
