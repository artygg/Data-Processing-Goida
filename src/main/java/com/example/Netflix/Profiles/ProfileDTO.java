package com.example.Netflix.Profiles;

public class ProfileDTO {
    private String language;
    private String profileName;
    private String profilePhoto;
    private String age;

    public ProfileDTO() {

    }

    public ProfileDTO(String language,
                      String profileName,
                      String profilePhoto,
                      String age) {
        this.language = language;
        this.profileName = profileName;
        this.profilePhoto = profilePhoto;
        this.age = age;
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
