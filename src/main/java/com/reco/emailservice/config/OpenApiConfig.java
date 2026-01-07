package com.reco.emailservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

/**
 * Swagger / OpenAPI configuration exposed only when the "dev" profile is
 * active.
 */
@Configuration
@Profile("dev")
public class OpenApiConfig {

    private static final String SERVICE_AUTH_HEADER = "x-Service-Authorization";
    private static final String SECURITY_BEARER = "BearerAuth";
    private static final String SECURITY_SERVICE = "ServiceAuth";

    @Bean
    public GroupedOpenApi emailServiceApi() {
        return GroupedOpenApi.builder()
                .group("email-service")
                .packagesToScan("com.reco.emailservice")
                .build();
    }

    @Bean
    public OpenAPI emailServiceOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .name(HttpHeaders.AUTHORIZATION)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        SecurityScheme serviceHeaderScheme = new SecurityScheme()
                .name(SERVICE_AUTH_HEADER)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);

        Components components = new Components()
                .addSecuritySchemes(SECURITY_BEARER, bearerScheme)
                .addSecuritySchemes(SECURITY_SERVICE, serviceHeaderScheme);

        SecurityRequirement requirement = new SecurityRequirement()
                .addList(SECURITY_BEARER)
                .addList(SECURITY_SERVICE);

        return new OpenAPI()
                .components(components)
                .addSecurityItem(requirement)
                .info(new Info()
                        .title("Reco Email Service API")
                        .version("v1")
                        .description("Email delivery endpoints exposed for development testing.")
                        .contact(new Contact().name("RecoTrack Team")));
    }
}
