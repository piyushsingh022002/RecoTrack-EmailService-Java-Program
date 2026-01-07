package com.reco.emailservice.controller;

import com.reco.emailservice.model.EmailActionRequest;
import com.reco.emailservice.service.EmailService;
import jakarta.validation.Valid;

import com.reco.emailservice.security.UserPrincipal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('SCOPE_EMAIL_SEND')")
    public void sendEmail(@Valid @RequestBody EmailActionRequest request) {
        // Extract authenticated user from SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserPrincipal user)) {
            throw new IllegalStateException("Authenticated user not found");
        }

        emailService.process(request, user);
    }

}
