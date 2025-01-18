package com.example.Netflix.Subtitle;

import com.example.Netflix.Content.Content;
import com.example.Netflix.enums.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@IdClass(SubtitleId.class)
@Table(name = "subtitles")
public class Subtitle {
    @Id
    @Column(name = "content_id")
    private Long contentId;

    @Id
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Language is require")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Language must contain only letters")
    private Language language;

    @ManyToOne
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    @NotBlank(message = "Content is required")
    private Content content;

    public Subtitle()
    {
    }

    public Long getContentId()
    {
        return contentId;
    }

    public void setContentId(Long contentId)
    {
        this.contentId = contentId;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage(Language language)
    {
        this.language = language;
    }

    public Content getContent()
    {
        return content;
    }

    public void setContent(Content content)
    {
        this.content = content;
    }
}