package com.example.Netflix.ApiUsers;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Table(name = "api_users")
@Entity
public class ApiUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    @NotBlank(message = "Login is required")
    private String login;
    @NotBlank(message = "Password is required")
    private String password;
    private String token;

    public ApiUser() {

    }

    public ApiUser(String login,
                   String password) {
        this.login = login;
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
