package com.example.Netflix.RefreshTokens;

public class RefreshTokenDTO {
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

