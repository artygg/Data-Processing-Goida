package com.example.Netflix.RefreshTokens;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Table(name = "refresh_tokens")
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    private String username;
    private Date expiryDate;

    public RefreshToken() {

    }

    public RefreshToken(String token,
                        String username,
                        Date expiryDate) {
        this.token = token;
        this.username = username;
        this.expiryDate = expiryDate;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
