package com.reco.emailservice.service;

import com.reco.emailservice.EmailSender.EmailSender;
import com.reco.emailservice.model.EmailAction;
import com.reco.emailservice.model.EmailActionRequest;
import com.reco.emailservice.model.ForgotPasswordOtpRequest;
import com.reco.emailservice.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Email Service
 *
 * Handles email processing, template resolution, and delivery.
 * Supports multiple authentication scenarios:
 * - User JWT authentication
 * - Service token authentication
 * - Combined authentication for critical operations
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final EmailTemplateResolver resolver;
    private final EmailSender emailSender;

    public EmailService(EmailTemplateResolver resolver, EmailSender emailSender) {
        this.resolver = resolver;
        this.emailSender = emailSender;
    }

    /**
     * Process email request with user JWT authentication.
     *
     * Validates action, resolves template, and sends email using user context.
     *
     * @param request payload containing actionId
     * @param user    authenticated user extracted from JWT
     * @throws IllegalArgumentException if email action is invalid
     */
    public void process(EmailActionRequest request, UserPrincipal user) {
        // 1️⃣ Validate and map action code to enum
        EmailAction action;
        try {
            action = EmailAction.fromCode(request.getActionId());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid Email Action code: " + request.getActionId(), ex);
        }

        // 2️⃣ Resolve email template using action and user info
        EmailTemplate template = resolver.resolve(action, user);

        // 3️⃣ Send email
        emailSender.send(user.getEmail(), template.subject(), template.body());
    }

    /**
     * Process email request with service token authentication.
     *
     * Used for service-to-service email operations.
     * Service context is used instead of user context.
     *
     * @param request payload containing actionId
     * @throws IllegalArgumentException if email action is invalid
     */
    public void processWithServiceToken(EmailActionRequest request) {
        // 1️⃣ Validate and map action code to enum
        EmailAction action;
        try {
            action = EmailAction.fromCode(request.getActionId());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid Email Action code: " + request.getActionId(), ex);
        }

        // 2️⃣ Resolve email template using action (service context)
        // In a real scenario, you might use a different resolver or skip user
        // resolution
        // For now, using the standard resolver with null user context
        EmailTemplate template = resolver.resolve(action, null);

        // 3️⃣ Send email (in production, this would use the resolved email from
        // request)
        emailSender.send("service-notification@recotrack.com", template.subject(), template.body());
    }

    /**
     * Send forgot-password OTP via service-to-service call.
     *
     * @param request payload containing email, otp, and action type
     */
    public void sendForgotPasswordOtp(ForgotPasswordOtpRequest request) {
        if (request.getActionType() != EmailAction.FORGOT_PASSWORD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "actionType must be FORGOT_PASSWORD");
        }

        EmailTemplate template = resolver.resolveForgotPasswordOtp(request.getOtp());

        log.info("Sending forgot-password OTP email to {}", maskEmail(request.getEmail()));
        emailSender.send(request.getEmail(), template.subject(), template.body());
    }

    /**
     * Process critical email request requiring both user JWT and service token.
     *
     * Maximum security for sensitive operations.
     * Both user context and service authentication are validated.
     *
     * @param request payload containing actionId
     * @param user    authenticated user extracted from JWT
     * @throws IllegalArgumentException if email action is invalid
     */
    public void processCritical(EmailActionRequest request, UserPrincipal user) {
        // 1️⃣ Validate and map action code to enum
        EmailAction action;
        try {
            action = EmailAction.fromCode(request.getActionId());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid Email Action code: " + request.getActionId(), ex);
        }

        // 2️⃣ Resolve email template using action and user info
        EmailTemplate template = resolver.resolve(action, user);

        // 3️⃣ Log critical operation (for audit trail)
        System.out.println("CRITICAL EMAIL: User=" + user.getUsername() + ", Action=" + request.getActionId());

        // 4️⃣ Send email
        emailSender.send(user.getEmail(), template.subject(), template.body());
    }

    /**
     * Verify email delivery status.
     *
     * Public endpoint for checking if an email has been processed.
     *
     * @param emailId The email identifier to verify
     * @return true if email exists, false otherwise
     */
    public boolean verifyEmail(String emailId) {
        // In a real implementation, query the database for email status
        // This is a placeholder for the verification logic
        return !emailId.isEmpty();
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return "***" + (atIndex >= 0 ? email.substring(atIndex) : "");
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}