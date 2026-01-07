package com.reco.emailservice.service;

/**
 * Immutable DTO representing an email message.
 */
public class EmailTemplate {

    private final String subject;
    private final String body;

    public EmailTemplate(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String subject() {
        return subject;
    }

    public String body() {
        return body;
    }

    @Override
    public String toString() {
        return "EmailTemplate{subject='" + subject + "', body='" + body + "'}";
    }
}