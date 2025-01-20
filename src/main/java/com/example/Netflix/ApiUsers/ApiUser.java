package com.example.Netflix.ApiUsers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Table(name = "api_users")
@Entity
public class ApiUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier of the user")
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Login is required")
    @Schema(description = "Login of the user", example = "john_doe")
    private String login;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password of the user")
    private String password;

    @Schema(description = "Token assigned to the user after login")
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
