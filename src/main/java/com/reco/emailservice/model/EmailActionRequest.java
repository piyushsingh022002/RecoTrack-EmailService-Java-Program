package com.reco.emailservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmailActionRequest {

    @NotBlank
    public String actionId;

    @NotNull
    public UserInfo user;

    public Metadata metadata;

    public static class UserInfo {
        @NotBlank
        public String id;

        @NotBlank
        public String username;

        @Email
        public String email;
    }

    public static class Metadata {
        public String source;
        public String locale;
    }
}
