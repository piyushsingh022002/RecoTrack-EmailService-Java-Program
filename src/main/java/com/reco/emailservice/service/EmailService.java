package com.reco.emailservice.service;

import com.reco.emailservice.EmailSender.EmailSender;
import com.reco.emailservice.domain.EmailAction;
import com.reco.emailservice.model.EmailActionRequest;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailTemplateResolver resolver;
    private final EmailSender emailSender;

    public EmailService(EmailTemplateResolver resolver, EmailSender emailSender) {
        this.resolver = resolver;
        this.emailSender = emailSender;
    }

    public void process(EmailActionRequest request) {

        EmailAction action = EmailAction.valueOf(request.actionId);

        var template = resolver.resolve(action, request);

        // send email (SMTP / SendGrid later)
        emailSender.send(
                request.user.email,
                template.subject(),
                template.body());
    }
}