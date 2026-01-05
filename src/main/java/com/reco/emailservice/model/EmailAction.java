package com.reco.emailservice.model;

/**
 * Enum representing all possible email actions in the system.
 * Used to map action codes from API requests to concrete email templates.
 */
public enum EmailAction {
    WELCOME,
    PASSWORD_RESET,
    NOTIFICATION;

    public static EmailAction fromCode(String code) {
        for (EmailAction action : values()) {
            if (action.name().equalsIgnoreCase(code)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid Email Action code: " + code);
    }
}