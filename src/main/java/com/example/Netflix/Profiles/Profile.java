package com.example.Netflix.Profiles;

import com.example.Netflix.Content.Content;
import com.example.Netflix.Preferences.Preferences;
import com.example.Netflix.Users.User;
import com.example.Netflix.WatchHistory.WatchHistory;
import com.example.Netflix.enums.Language;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @JsonBackReference
    private User user;
    private String profileName;
    private String profilePhoto;
    private LocalDate age;
    @Enumerated(EnumType.STRING)
    private Language language;
    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private Preferences preferences;
    @OneToMany(mappedBy = "profileId")
    private List<WatchHistory> watchHistories;
    @OneToMany
    private List<Content> watchList;
    @ManyToMany
    private List<Content> watchLater;
    private boolean isChild;

    public Profile() {

    }

    public Profile(User user,
                   String profilePhoto,
                   String profileName,
                   LocalDate age,
                   Preferences preferences) {
        this.user = user;
        this.profilePhoto = profilePhoto;
        this.profileName = profileName;
        this.age = age;
        this.language = Language.ENGLISH;
        this.preferences = preferences;
        this.isChild = false;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDate getAge() {
        return this.age;
    }

    public List<Content> getWatchLater() {
        return this.watchLater;
    }

    public void setWatchLater(List<Content> watchLater) {
        this.watchLater = watchLater;
    }

    public void setAge(LocalDate age) {
        this.age = age;
        setChild(age.getYear() - LocalDate.now().getYear() >= 18);
    }

    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Preferences getPreferences() {
        return this.preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public List<WatchHistory> getWatchHistories() {
        return this.watchHistories;
    }

    public void setWatchHistories(List<WatchHistory> watchHistories) {
        this.watchHistories = watchHistories;
    }

    public List<Content> getWatchList() {
        return this.watchList;
    }

    public void setWatchList(List<Content> watchList) {
        this.watchList = watchList;
    }

    public boolean isChild() {
        return this.isChild;
    }

    public void setChild(boolean child) {
        isChild = child;
    }

    public void addWatchLater(Content content) {
        this.watchLater.add(content);
    }
}
