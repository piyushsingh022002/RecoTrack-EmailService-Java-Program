package com.reco.emailservice.service;

import com.reco.emailservice.model.EmailAction;
import com.reco.emailservice.security.UserPrincipal;
import org.springframework.stereotype.Component;

/**
 * Resolves email templates based on EmailAction and user info.
 */
@Component
public class EmailTemplateResolver {

    /**
     * Build email template for given action and user.
     *
     * @param action EmailAction enum
     * @param user   Authenticated user from JWT
     * @return EmailTemplate containing subject and body
     */
    public EmailTemplate resolve(EmailAction action, UserPrincipal user) {
        String subject;
        String body;

        switch (action) {
            case WELCOME -> {
                subject = "Welcome " + user.getUsername();
                body = "Hi " + user.getUsername() + ", welcome to RecoTrack!";
            }
            case PASSWORD_RESET -> {
                subject = "Password Reset";
                body = "Hi " + user.getUsername() + ", reset your password here: <link>";
            }
            case NOTIFICATION -> {
                subject = "Notification";
                body = "Hi " + user.getUsername() + ", you have a new notification.";
            }
            case FORGOT_PASSWORD -> {
                throw new IllegalArgumentException("Use resolveForgotPasswordOtp for FORGOT_PASSWORD action");
            }
            default -> throw new IllegalArgumentException("Unsupported EmailAction: " + action);
        }

        return new EmailTemplate(subject, body);
    }

    /**
     * Build email template for forgot-password OTP flow.
     */
    public EmailTemplate resolveForgotPasswordOtp(String otp) {
        String subject = "RecoTrack Password Reset OTP";
        String body = """
                <h2>Password Reset Request</h2>
                <p>We received a request to reset your password.</p>
                <p>Your one-time password (OTP) is <strong>%s</strong>.</p>
                <p>This code will expire in 5 minutes.</p>
                <p>If you did not request this, please ignore this email and do not share the code with anyone.</p>
                """.formatted(otp);

        return new EmailTemplate(subject, body);
    }

}