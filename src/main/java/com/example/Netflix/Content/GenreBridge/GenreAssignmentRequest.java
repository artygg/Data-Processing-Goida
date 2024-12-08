package com.example.Netflix.Content.GenreBridge;

import java.util.List;

public class GenreAssignmentRequest {
    private Integer contentId;
    private List<Long> genreIds;

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }
}
