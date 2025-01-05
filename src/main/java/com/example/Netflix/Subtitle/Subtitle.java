package com.example.Netflix.Subtitle;

import com.example.Netflix.Movie.Movie;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subtitles")
@IdClass(SubtitleId.class)
public class Subtitle {
    @Id
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Id
    private String language;

    // Getters and Setters
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

}

class SubtitleId implements Serializable {
    private Long movie;
    private String language;

    // Getters and Setters, equals(), and hashCode() (for composite keys)
}