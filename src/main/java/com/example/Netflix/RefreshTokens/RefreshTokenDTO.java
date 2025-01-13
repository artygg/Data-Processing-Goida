package com.example.Netflix.RefreshTokens;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenDTO {
    @NotBlank(message = "Token is required")
    private String token;

    public RefreshTokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

