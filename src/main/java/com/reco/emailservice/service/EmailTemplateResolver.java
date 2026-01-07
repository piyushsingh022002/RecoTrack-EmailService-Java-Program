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
            default -> throw new IllegalArgumentException("Unsupported EmailAction: " + action);
        }

        return new EmailTemplate(subject, body);
    }

}