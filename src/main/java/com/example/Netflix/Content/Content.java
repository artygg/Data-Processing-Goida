package com.example.Netflix.Content;

import com.example.Netflix.Content.GenreBridge.GenreBridge;
import com.example.Netflix.Content.Movie.Movie;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "contents")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String poster;

    private String description;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    private Integer season;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Movie> movies;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GenreBridge> genres;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ContentType getType() { return type; }
    public void setType(ContentType type) { this.type = type; }

    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<Movie> getMovies() { return movies; }
    public void setMovies(Set<Movie> movies) { this.movies = movies; }

    public Set<GenreBridge> getGenres() { return genres; }
    public void setGenres(Set<GenreBridge> genres) { this.genres = genres; }

}
