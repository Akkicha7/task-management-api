package com.taskproject.dto;

import java.time.Instant;

/**
 * DTO returned after a successful authentication.
 */
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Instant expiresAt;

    public AuthResponse() {}
    public AuthResponse(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
