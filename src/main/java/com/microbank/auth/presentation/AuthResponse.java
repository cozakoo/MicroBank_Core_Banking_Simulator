package com.microbank.auth.presentation;

import java.util.UUID;

public class AuthResponse {

    private String token;
    private String username;
    private UUID userId;

    public AuthResponse(String token, String username, UUID userId) {
        this.token = token;
        this.username = username;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public UUID getUserId() { return userId; }
}
