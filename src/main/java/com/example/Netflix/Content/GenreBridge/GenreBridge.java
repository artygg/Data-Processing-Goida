package com.example.Netflix.Content.GenreBridge;

import com.example.Netflix.Content.Content;
import com.example.Netflix.Content.Genre.Genre;
import jakarta.persistence.*;

@Entity
@Table(name = "genre_bridges")
public class GenreBridge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }


}
