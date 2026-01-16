package com.reco.emailservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 Configuration for Email Service API.
 *
 * Configures Swagger/OpenAPI documentation with:
 * - HTTP Bearer (JWT) authentication for user tokens
 * - API Key authentication for service-to-service communication
 * - Multiple security scheme combinations
 * - Exposed at: /swagger-ui.html (UI) and /v3/api-docs (JSON spec)
 */
@Configuration
public class OpenApiConfig {

        // Security scheme identifiers used in annotations
        public static final String SECURITY_SCHEME_USER_JWT = "UserJWT";
        public static final String SECURITY_SCHEME_SERVICE_TOKEN = "ServiceToken";

        // HTTP Headers
        private static final String HEADER_AUTHORIZATION = "Authorization";
        private static final String HEADER_SERVICE_TOKEN = "X-Service-Token";

        /**
         * Defines the primary API group for OpenAPI documentation
         */
        @Bean
        public GroupedOpenApi emailServiceApi() {
                return GroupedOpenApi.builder()
                                .group("email-service")
                                .pathsToMatch("/api/**")
                                .packagesToScan("com.reco.emailservice.controller")
                                .build();
        }

        /**
         * Configures the OpenAPI specification with security schemes and general info
         */
        @Bean
        public OpenAPI emailServiceOpenAPI() {
                // HTTP Bearer JWT Authentication Scheme
                SecurityScheme userJwtScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name(HEADER_AUTHORIZATION)
                                .in(SecurityScheme.In.HEADER)
                                .description("JWT token issued to users for API authentication. " +
                                                "Include in the Authorization header as: Bearer <token>");

                // API Key Authentication Scheme for Service-to-Service Communication
                SecurityScheme serviceTokenScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .name(HEADER_SERVICE_TOKEN)
                                .in(SecurityScheme.In.HEADER)
                                .description("Service token for service-to-service authentication. " +
                                                "Include in the X-Service-Token header as: <token>");

                // Register both security schemes in components
                Components components = new Components()
                                .addSecuritySchemes(SECURITY_SCHEME_USER_JWT, userJwtScheme)
                                .addSecuritySchemes(SECURITY_SCHEME_SERVICE_TOKEN, serviceTokenScheme);

                // Build and return OpenAPI spec
                return new OpenAPI()
                                .components(components)
                                .info(buildApiInfo())
                                .addServersItem(buildLocalServer());
        }

        /**
         * Builds comprehensive API information
         */
        private Info buildApiInfo() {
                return new Info()
                                .title("RecoTrack Email Service API")
                                .version("1.0.0")
                                .description("""
                                                Email Service API for managing email delivery operations.

                                                ## Authentication

                                                This API supports two authentication methods:

                                                1. **User JWT Token** (HTTP Bearer)
                                                   - Type: HTTP Bearer with JWT
                                                   - Header: `Authorization: Bearer <jwt-token>`
                                                   - Used for user-initiated email operations
                                                   - Required scope: `EMAIL_SEND`

                                                2. **Service Token** (API Key)
                                                   - Type: API Key in header
                                                   - Header: `X-Service-Token: <service-token>`
                                                   - Used for service-to-service communication
                                                   - Can be combined with User JWT for enhanced security

                                                ## Usage

                                                - **User Token Only**: Authenticate as a user to send emails
                                                - **Service Token Only**: Service-to-service API calls
                                                - **Both Tokens**: Maximum security for critical operations
                                                """)
                                .contact(new Contact()
                                                .name("RecoTrack Team")
                                                .email("support@recotrack.com")
                                                .url("https://recotrack.com"))
                                .license(new License()
                                                .name("Apache 2.0")
                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
        }

        /**
         * Builds server information (localhost for development)
         */
        private Server buildLocalServer() {
                return new Server()
                                .url("http://localhost:8080")
                                .description("Development server");
        }
}
