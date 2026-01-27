package com.reco.emailservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Request payload for sending a forgot-password OTP email.
 */
@Schema(name = "ForgotPasswordOtpRequest", description = "Service-to-service request to send an OTP for password reset")
public class ForgotPasswordOtpRequest {

    @Email(message = "email must be valid")
    @NotBlank(message = "email is required")
    @Schema(description = "Recipient email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "otp is required")
    @Pattern(regexp = "\\d{6}", message = "otp must be exactly 6 digits")
    @Schema(description = "6 digit one-time password", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;

    @NotNull(message = "actionType is required")
    @Schema(description = "Must be FORGOT_PASSWORD for this operation", example = "FORGOT_PASSWORD", requiredMode = Schema.RequiredMode.REQUIRED)
    private EmailAction actionType;

    public ForgotPasswordOtpRequest() {
    }

    public ForgotPasswordOtpRequest(String email, String otp, EmailAction actionType) {
        this.email = email;
        this.otp = otp;
        this.actionType = actionType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public EmailAction getActionType() {
        return actionType;
    }

    public void setActionType(EmailAction actionType) {
        this.actionType = actionType;
    }
}
