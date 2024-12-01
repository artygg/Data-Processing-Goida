package com.example.Netflix.Preferences;

import com.example.Netflix.enums.Classification;
import com.example.Netflix.enums.Genre;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class PreferencesRequest {
    private boolean isInterestedInSeries;
    private boolean isInterestedInFilms;
    private List<String> genres;
    private List<String> classifications;
    private boolean isInterestedInFilmsWithMinimumAge;

    public PreferencesRequest() {

    }

    public PreferencesRequest(boolean isInterestedInSeries,
                              boolean isInterestedInFilms,
                              boolean isInterestedInFilmsWithMinimumAge) {
        this.isInterestedInSeries = isInterestedInSeries;
        this.isInterestedInFilms = isInterestedInFilms;
        this.isInterestedInFilmsWithMinimumAge = isInterestedInFilmsWithMinimumAge;
        this.genres = new ArrayList<>();
        this.classifications = new ArrayList<>();
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

    public List<String> getGenres() {
        return this.genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getClassifications() {
        return this.classifications;
    }

    public void setClassifications(List<String> classifications) {
        this.classifications = classifications;
    }

    public boolean isInterestedInFilmsWithMinimumAge() {
        return this.isInterestedInFilmsWithMinimumAge;
    }

    public void setInterestedInFilmsWithMinimumAge(boolean interestedInFilmsWithMinimumAge) {
        isInterestedInFilmsWithMinimumAge = interestedInFilmsWithMinimumAge;
    }
}
