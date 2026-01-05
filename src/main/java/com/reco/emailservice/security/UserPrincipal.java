package com.reco.emailservice.security;

public class UserPrincipal {

    private final String userId;
    private final String username;
    private final String email;

    public UserPrincipal(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}