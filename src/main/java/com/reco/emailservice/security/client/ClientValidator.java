package com.reco.emailservice.security.client;

import java.util.Set;

public class ClientValidator {

    private final Set<String> allowedClients;

    public ClientValidator(Set<String> allowedClients) {
        this.allowedClients = allowedClients;
    }

    public void validate(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Missing X-CLIENT-ID");
        }

        if (!allowedClients.contains(clientId)) {
            throw new IllegalArgumentException("Invalid client ID");
        }
    }
}
