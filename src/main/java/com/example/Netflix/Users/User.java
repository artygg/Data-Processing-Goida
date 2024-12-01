package com.example.Netflix.Users;

import com.example.Netflix.Exceptions.ProfileLimitReached;
import com.example.Netflix.Profiles.Profile;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String email;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Profile> profiles;
    @ElementCollection
    private Map<String, Integer> amountOfViewedFilms;
    private int loginAttempt;
    private boolean isBanned;
    private String token;
    private LocalDateTime banUntil;

    public User() {

    }

    public User(String email,
                String password,
                int loginAttempt,
                String token,
                LocalDateTime banUntil) {
        this.email = email;
        this.password = password;
        this.loginAttempt = loginAttempt;
        this.isBanned = false;
        this.profiles = new ArrayList<>();
        this.amountOfViewedFilms = new HashMap<>();
        this.token = token;
        this.banUntil = banUntil;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public Map<String, Integer> getAmountOfViewedFilms() {
        return this.amountOfViewedFilms;
    }

    public void setAmountOfViewedFilms(HashMap<String, Integer> amountOfViewedFilms) {
        this.amountOfViewedFilms = amountOfViewedFilms;
    }

    public int getLoginAttempt() {
        return this.loginAttempt;
    }

    public void setLoginAttempt(int loginAttempt) {
        this.loginAttempt = loginAttempt;
    }

    public boolean isBanned() {
        return this.isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public void addProfile(Profile profile) throws ProfileLimitReached {
        if (this.profiles.size() + 1 <= 4) {
            this.profiles.add(profile);
        } else {
            throw new ProfileLimitReached();
        }
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAmountOfViewedFilms(Map<String, Integer> amountOfViewedFilms) {
        this.amountOfViewedFilms = amountOfViewedFilms;
    }

    public LocalDateTime getBanUntil() {
        return this.banUntil;
    }

    public void setBanUntil(LocalDateTime banUntil) {
        this.banUntil = banUntil;
    }
}
