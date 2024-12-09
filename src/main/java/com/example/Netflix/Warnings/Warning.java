package com.example.Netflix.Warnings;

import com.example.Netflix.Users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warnings")
public class Warning {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToOne
    @JsonBackReference
    private User user;
    private int loginFaults;
    private LocalDateTime banEndDate;

    public Warning() {

    }

    public Warning (User user,
                    int loginFaults,
                    LocalDateTime banEndDate) {
        this.user = user;
        this.loginFaults = loginFaults;
        this.banEndDate = banEndDate;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLoginFaults() {
        return this.loginFaults;
    }

    public void setLoginFaults(int loginFaults) {
        this.loginFaults = loginFaults;
    }

    public LocalDateTime getBanEndDate() {
        return this.banEndDate;
    }

    public void setBanEndDate(LocalDateTime banEndDate) {
        this.banEndDate = banEndDate;
    }
}
