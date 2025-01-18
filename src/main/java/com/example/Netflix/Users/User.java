package com.example.Netflix.Users;

import com.example.Netflix.Exceptions.ProfileLimitReached;
import com.example.Netflix.Profiles.Profile;
import com.example.Netflix.Warnings.Warning;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotBlank(message = "Email is required")
    @Pattern(regexp = ".*@.*mail\\.com$", message = "Invalid format of an email address")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter and one special character"
    )
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Profile> profiles;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Warning warning;
    private boolean hasUsedReferralLink;
    private boolean isBanned;
    private String token;


    public User() {

    }

    public User(String email,
                String password,
                String token,
                boolean hasUsedReferralLink
                ) {
        this.email = email;
        this.password = password;
        this.isBanned = false;
        this.profiles = new ArrayList<>();
        this.token = token;
        this.hasUsedReferralLink = hasUsedReferralLink;
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

    public Warning getWarning() {
        return this.warning;
    }

    public void setWarning(Warning warning) {
        this.warning = warning;
    }

    public boolean isHasUsedReferralLink() {
        return this.hasUsedReferralLink;
    }

    public void setHasUsedReferralLink(boolean hasUsedReferralLink) {
        this.hasUsedReferralLink = hasUsedReferralLink;
    }

}
