package com.example.Netflix.Preferences;

import com.example.Netflix.Genre.Genre;
import com.example.Netflix.Profiles.Profile;
import com.example.Netflix.enums.Classification;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preferences")
public class Preferences {
    @Id
    private int id;
    private boolean isInterestedInSeries;
    private boolean isInterestedInFilms;
    @OneToMany
    private List<Genre> genres;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Classification> classifications;
    private boolean isInterestedInFilmsWithMinimumAge;
    @OneToOne
    @JsonBackReference
    private Profile profile;

    public Preferences() {

    }

    public Preferences(boolean isInterestedInSeries,
                       boolean isInterestedInFilms,
                       boolean isInterestedInFilmsWithMinimumAge) {
        this.isInterestedInSeries = isInterestedInSeries;
        this.isInterestedInFilms = isInterestedInFilms;
        this.genres = new ArrayList<>();
        this.classifications = new ArrayList<>();
        this.isInterestedInFilmsWithMinimumAge = isInterestedInFilmsWithMinimumAge;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isInterestedInSeries() {
        return this.isInterestedInSeries;
    }

    public void setInterestedInSeries(boolean interestedInSeries) {
        isInterestedInSeries = interestedInSeries;
    }

    public boolean isInterestedInFilms() {
        return this.isInterestedInFilms;
    }

    public void setInterestedInFilms(boolean interestedInFilms) {
        isInterestedInFilms = interestedInFilms;
    }

    public List<Genre> getGenres() {
        return this.genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Classification> getClassifications() {
        return this.classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    public boolean isInterestedInFilmsWithMinimumAge() {
        return this.isInterestedInFilmsWithMinimumAge;
    }

    public void setInterestedInFilmsWithMinimumAge(boolean interestedInFilmsWithMinimumAge) {
        isInterestedInFilmsWithMinimumAge = interestedInFilmsWithMinimumAge;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
