package com.reco.emailservice.EmailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Responsible for sending emails via SMTP (or other configured mail server).
 */
@Component
public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final JavaMailSender mailSender;
    private final AtomicBoolean configLogged = new AtomicBoolean(false);

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send email to given recipient.
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param body    email body
     */
    public void send(String to, String subject, String body) {
        logMailConfigurationOnce();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.setFrom("workspace.piyush01@gmail.com");

            mailSender.send(message);
            log.info("Email sent to {}", maskEmail(to));
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", maskEmail(to), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Mask email for safe logging (show only first char + domain)
     */
    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1)
            return "***" + email.substring(atIndex);
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    private void logMailConfigurationOnce() {
        if (configLogged.get() || !(mailSender instanceof JavaMailSenderImpl impl)) {
            return;
        }
        if (configLogged.compareAndSet(false, true)) {
            Properties props = impl.getJavaMailProperties();
            log.info(
                    "Mail config -> host: {}, port: {}, username: {}, protocol: {}, starttls: {}, auth: {}, passwordSet: {}",
                    impl.getHost(),
                    impl.getPort(),
                    maskValue(impl.getUsername()),
                    impl.getProtocol(),
                    props.getProperty("mail.smtp.starttls.enable"),
                    props.getProperty("mail.smtp.auth"),
                    impl.getPassword() != null && !impl.getPassword().isBlank());
        }
    }

    private String maskValue(String value) {
        if (value == null || value.isBlank()) {
            return "n/a";
        }
        if (value.length() <= 2) {
            return "***";
        }
        return value.charAt(0) + "***" + value.charAt(value.length() - 1);
    }
}
