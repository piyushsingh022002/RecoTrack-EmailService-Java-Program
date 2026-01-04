package com.reco.emailservice.service;

import com.reco.emailservice.domain.EmailAction;
import com.reco.emailservice.model.EmailActionRequest;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailTemplateResolver resolver;

    public EmailService(EmailTemplateResolver resolver) {
        this.resolver = resolver;
    }

    public void process(EmailActionRequest request) {

        EmailAction action = EmailAction.valueOf(request.actionId);

        var template = resolver.resolve(action, request);

        // send email (SMTP / SendGrid later)
        System.out.println("Sending to: " + request.user.email);
        System.out.println("Subject: " + template.subject());
        System.out.println("Body: " + template.body());
    }
}