package com.reco.emailservice.EmailSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Responsible for sending emails via Brevo HTTP API.
 */
@Component
public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate;
    private final AtomicBoolean configLogged = new AtomicBoolean(false);

    public EmailSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Send email to given recipient.
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param body    email body
     */
    public void send(String to, String subject, String body) {
        logMailConfigurationOnce();
        try {
            // Build request payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", Map.of("name", "PiyushWorkspace", "email", "workspace.piyush01@gmail.com"));
            payload.put("to", List.of(Map.of("email", to)));
            payload.put("subject", subject);
            payload.put("htmlContent", body);

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey.trim());

            // Create HTTP entity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            // Send request
            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class);

            log.info("Email sent to {}", maskEmail(to));
        } catch (Exception e) {
            log.error("Failed to send email to {}", maskEmail(to), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Mask email for safe logging (show only first char + domain)
     */
    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1)
            return "***" + email.substring(atIndex);
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    private void logMailConfigurationOnce() {
        if (configLogged.compareAndSet(false, true)) {
            log.info(
                    "Mail config -> provider: Brevo HTTP API, url: {}, apiKeySet: {}",
                    BREVO_API_URL,
                    brevoApiKey != null && !brevoApiKey.isBlank());
        }
    }
}
