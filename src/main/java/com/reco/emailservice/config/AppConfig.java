package com.reco.emailservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application-wide configuration for common beans.
 */
@Configuration
public class AppConfig {

    /**
     * Provides a RestTemplate bean for making HTTP requests.
     * Used by EmailSender to communicate with Brevo API.
     *
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
