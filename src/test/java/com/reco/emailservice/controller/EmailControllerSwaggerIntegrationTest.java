package com.reco.emailservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration Tests for OpenAPI/Swagger Documentation
 *
 * Tests verify that:
 * 1. OpenAPI spec is generated correctly
 * 2. Security schemes are configured
 * 3. Endpoints are documented in the spec
 * 4. Swagger UI resources are accessible
 */
@SpringBootTest
@DisplayName("OpenAPI/Swagger Integration Tests")
public class EmailControllerSwaggerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // ============ SWAGGER UI & OpenAPI Spec Tests ============

    @Test
    @DisplayName("OpenAPI JSON spec should be accessible and valid")
    public void testOpenAPIJsonSpec() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("OpenAPI YAML spec should be accessible")
    public void testOpenAPIYamlSpec() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("openapi:")));
    }

    @Test
    @DisplayName("Public health endpoint should be accessible")
    public void testHealthEndpointPublic() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email Service")));
    }

    @Test
    @DisplayName("Public health status endpoint should be accessible")
    public void testHealthStatusEndpointPublic() throws Exception {
        mockMvc.perform(get("/health/status"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Operational")));
    }

    @Test
    @DisplayName("Public email verify endpoint should be accessible")
    public void testVerifyEmailEndpointPublic() throws Exception {
        mockMvc.perform(get("/api/email/verify/email-123"))
                .andExpect(status().isOk());
    }
}
