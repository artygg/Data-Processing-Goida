package com.example.Netflix.Content;

import com.example.Netflix.Genre.Genre;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String videoLink;
    private Double duration;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    private Integer season;
    private Integer episodeNumber;
    private Integer seriesId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "genre_contents",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    public Content()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getPoster()
    {
        return poster;
    }

    public void setPoster(String poster)
    {
        this.poster = poster;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getVideoLink()
    {
        return videoLink;
    }

    public void setVideoLink(String videoLink)
    {
        this.videoLink = videoLink;
    }

    public Double getDuration()
    {
        return duration;
    }

    public void setDuration(Double duration)
    {
        this.duration = duration;
    }

    public ContentType getType()
    {
        return type;
    }

    public void setType(ContentType type)
    {
        this.type = type;
    }

    public Integer getSeason()
    {
        return season;
    }

    public void setSeason(Integer season)
    {
        this.season = season;
    }

    public Integer getEpisodeNumber()
    {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber)
    {
        this.episodeNumber = episodeNumber;
    }

    public Integer getSeriesId()
    {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId)
    {
        this.seriesId = seriesId;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Set<Genre> getGenres()
    {
        return genres;
    }

    public void setGenres(Set<Genre> genres)
    {
        this.genres = genres;
    }
}

