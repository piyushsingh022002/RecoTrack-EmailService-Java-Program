package com.reco.emailservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Email Action Request Payload
 *
 * Payload received from client for triggering email actions.
 * Contains the action code; user info is extracted from JWT or service token.
 */
@Schema(name = "EmailActionRequest", description = "Request payload for email action operations", example = "{ \"actionId\": \"action-12345\" }")
public class EmailActionRequest {

    @NotBlank(message = "actionId is required")
    @Schema(description = "Unique identifier for the email action", example = "action-12345", requiredMode = Schema.RequiredMode.REQUIRED)
    public String actionId;

    // Default constructor for deserialization
    public EmailActionRequest() {
    }

    /**
     * Create an EmailActionRequest with the specified action ID
     *
     * @param actionId Unique identifier for the email action
     */
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