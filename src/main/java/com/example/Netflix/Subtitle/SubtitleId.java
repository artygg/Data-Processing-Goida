package com.example.Netflix.Subtitle;

import java.io.Serializable;
import java.util.Objects;

public class SubtitleId implements Serializable {
    private Long contentId;
    private String language;

    public SubtitleId(Long contentId,
                      String language) {
        this.contentId = contentId;
        this.language = language;
    }

    public SubtitleId() {
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubtitleId that = (SubtitleId) o;
        return Objects.equals(contentId, that.contentId) && Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId, language);
    }
}