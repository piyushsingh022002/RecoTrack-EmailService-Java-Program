package com.reco.emailservice.service;

import com.reco.emailservice.EmailSender.EmailSender;
import com.reco.emailservice.model.EmailAction;
import com.reco.emailservice.model.EmailActionRequest;

import com.reco.emailservice.security.UserPrincipal;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailTemplateResolver resolver;
    private final EmailSender emailSender;

    public EmailService(EmailTemplateResolver resolver, EmailSender emailSender) {
        this.resolver = resolver;
        this.emailSender = emailSender;
    }

    /**
     * Process email request: validate action, build template, send email.
     *
     * @param request payload containing actionId
     * @param user    authenticated user extracted from JWT
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
}