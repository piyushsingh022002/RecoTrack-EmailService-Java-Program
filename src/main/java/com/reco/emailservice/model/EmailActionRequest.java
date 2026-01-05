package com.reco.emailservice.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload received from client for triggering email actions.
 * Contains only the action code; user info is extracted from JWT.
 */
public class EmailActionRequest {

    @NotBlank(message = "actionId is required")
    public String actionId;

    // Default constructor for deserialization
    public EmailActionRequest() {
    }

    public EmailActionRequest(String actionId) {
        this.actionId = actionId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }
}