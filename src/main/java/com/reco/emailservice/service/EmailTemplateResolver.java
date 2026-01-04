package com.reco.emailservice.service;

import com.reco.emailservice.domain.EmailAction;
import com.reco.emailservice.model.EmailActionRequest;
import com.reco.emailservice.model.EmailTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateResolver {

    public EmailTemplate resolve(EmailAction action, EmailActionRequest request) {

        return switch (action) {

            case USER_REGISTERED -> new EmailTemplate(
                    "Welcome " + request.user.username,
                    "Hi " + request.user.username + ", welcome to our platform!");

            case PASSWORD_RESET -> new EmailTemplate(
                    "Password Reset",
                    "Reset link for user " + request.user.username);

            default -> throw new IllegalArgumentException("Unsupported action");
        };
    }
}