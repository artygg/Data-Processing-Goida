package com.example.Netflix.Profiles;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ProfileDTO {
    @NotNull
    private UUID userID;
    @NotBlank(message = "Language is required")
    private String language;
    private String profileName;
    @NotBlank(message = "Profile image is required")
    private String profilePhoto;
    private String age;

    public ProfileDTO() {

    }

    public ProfileDTO(UUID userID,
                      String language,
                      String profileName,
                      String profilePhoto,
                      String age) {
        this.userID = userID;
        this.language = language;
        this.profileName = profileName;
        this.profilePhoto = profilePhoto;
        this.age = age;
    }

    public UUID getUserID() {
        return this.userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfilePhoto() {
        return this.profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
