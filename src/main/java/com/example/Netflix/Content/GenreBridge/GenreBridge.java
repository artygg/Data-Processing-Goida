package com.example.Netflix.Content.GenreBridge;

import com.example.Netflix.Content.Content;
import com.example.Netflix.Content.Genre.Genre;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "genre_bridges")
public class GenreBridge {

    @EmbeddedId
    private GenreBridgeKey id;

    @JsonIgnore
    @JsonBackReference
    @ManyToOne
    @MapsId("contentId")
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @JsonBackReference
    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public GenreBridge() {}

    public GenreBridge(Content content, Genre genre) {
        this.content = content;
        this.genre = genre;
        this.id = new GenreBridgeKey(content.getId(), genre.getId());
    }

    // Getters and Setters
    public GenreBridgeKey getId() {
        return id;
    }

    public void setId(GenreBridgeKey id) {
        this.id = id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

}


