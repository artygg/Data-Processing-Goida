package com.example.Netflix.Content.GenreBridge;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GenreBridgeKey implements Serializable {
    private Long contentId;
    private Long genreId;

    public GenreBridgeKey() {}

    public GenreBridgeKey(Long contentId, Long genreId) {
        this.contentId = contentId;
        this.genreId = genreId;
    }

    // Getters and Setters
    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreBridgeKey that = (GenreBridgeKey) o;
        return Objects.equals(contentId, that.contentId) &&
                Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId, genreId);
    }

    @Override
    public String toString() {
        return "GenreBridgeKey{" +
                "genreId=" + genreId +
                '}';
    }
}
