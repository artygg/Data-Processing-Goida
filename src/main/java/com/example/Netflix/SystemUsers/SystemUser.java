package com.example.Netflix.SystemUsers;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SystemUser {
    @Id
    private Long id;
    private String login;
    private String password;
    private String token;

    public SystemUser() {

    }

    public SystemUser(String login,
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
