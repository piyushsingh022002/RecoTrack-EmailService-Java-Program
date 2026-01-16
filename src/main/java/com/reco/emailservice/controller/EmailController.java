package com.reco.emailservice.controller;

import com.reco.emailservice.config.OpenApiConfig;
import com.reco.emailservice.model.EmailActionRequest;
import com.reco.emailservice.security.UserPrincipal;
import com.reco.emailservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Email Service API Controller
 *
 * Provides endpoints for email operations with flexible authentication:
 * - User JWT Token: For authenticated user operations
 * - Service Token: For service-to-service communication
 * - Combined: For enhanced security on critical operations
 */
@RestController
@RequestMapping("/api/email")
@Tag(name = "Email Service", description = "API for managing email delivery operations")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Send email with User JWT Token authentication only.
     *
     * This endpoint requires a valid User JWT token for authentication.
     * The authenticated user context is extracted and used for email operations.
     *
     * Security: User JWT Required
     *
     * @param request Email action request containing recipient, subject, and
     *                content
     * @return 200 OK if email sent successfully
     */
    @PostMapping("/send")
    @PreAuthorize("hasAuthority('SCOPE_EMAIL_SEND')")
    @Operation(summary = "Send email (User JWT)", description = "Send an email using authenticated user context. " +
            "Requires a valid User JWT token with EMAIL_SEND scope.", tags = { "Email Service" })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_USER_JWT)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions for EMAIL_SEND scope")
    })
    public ResponseEntity<Void> sendEmail(
            @Valid @RequestBody EmailActionRequest request) {
        // Extract authenticated user from SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserPrincipal user)) {
            throw new IllegalStateException("Authenticated user not found");
        }

        emailService.process(request, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Send email with Service Token authentication only.
     *
     * This endpoint requires a valid Service Token for authentication.
     * Used for service-to-service email operations.
     *
     * Security: Service Token Required
     *
     * @param request            Email action request
     * @param serviceTokenHeader The service token (for documentation purposes)
     * @return 200 OK if email sent successfully
     */
    @PostMapping("/send/service")
    @PreAuthorize("hasAuthority('SERVICE_EMAIL_SEND')")
    @Operation(summary = "Send email (Service Token)", description = "Send an email using service authentication. " +
            "Requires a valid Service Token for service-to-service communication.", tags = { "Email Service" })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_SERVICE_TOKEN)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid service token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - service not authorized")
    })
    public ResponseEntity<Void> sendEmailWithServiceToken(
            @Valid @RequestBody EmailActionRequest request,
            @Parameter(name = "X-Service-Token", description = "Service authentication token", in = ParameterIn.HEADER, required = true, example = "service-token-abc123") @RequestHeader(value = "X-Service-Token", required = false) String serviceTokenHeader) {
        // Service token is validated by SecurityConfig
        emailService.processWithServiceToken(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Send email with both User JWT and Service Token authentication.
     *
     * This endpoint requires both User JWT and Service Token for maximum security.
     * Useful for critical operations requiring dual authentication.
     *
     * Security: Both User JWT AND Service Token Required
     *
     * @param request            Email action request
     * @param serviceTokenHeader The service token (for documentation purposes)
     * @return 202 Accepted if email queued for processing
     */
    @PostMapping("/send/critical")
    @PreAuthorize("hasAuthority('SCOPE_EMAIL_SEND') and hasAuthority('SERVICE_EMAIL_CRITICAL')")
    @Operation(summary = "Send critical email (User JWT + Service Token)", description = "Send a critical email requiring both User JWT and Service Token authentication. "
            +
            "Maximum security for sensitive operations.", tags = { "Email Service" })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_USER_JWT)
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_SERVICE_TOKEN)
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Email accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Invalid email request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public ResponseEntity<Void> sendCriticalEmail(
            @Valid @RequestBody EmailActionRequest request,
            @Parameter(name = "X-Service-Token", description = "Service authentication token", in = ParameterIn.HEADER, required = true, example = "service-token-xyz789") @RequestHeader(value = "X-Service-Token", required = false) String serviceTokenHeader) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserPrincipal user)) {
            throw new IllegalStateException("Authenticated user not found");
        }

        emailService.processCritical(request, user);
        return ResponseEntity.accepted().build();
    }

    /**
     * Verify email without authentication (public endpoint).
     *
     * This endpoint is available without authentication.
     * Used for email verification callbacks and webhooks.
     *
     * Security: None (Public)
     *
     * @param emailId The email ID to verify
     * @return 200 OK if email exists
     */
    @GetMapping("/verify/{emailId}")
    @Operation(summary = "Verify email (Public)", description = "Public endpoint to verify email delivery status. " +
            "No authentication required.", tags = { "Email Service" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified"),
            @ApiResponse(responseCode = "404", description = "Email not found")
    })
    public ResponseEntity<Void> verifyEmail(
            @Parameter(name = "emailId", description = "Unique email identifier", example = "email-12345") @PathVariable String emailId) {
        boolean verified = emailService.verifyEmail(emailId);
        return verified ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
