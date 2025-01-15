package com.example.Netflix.Content;

import com.example.Netflix.Deserializer.StatusDeserializer;
import com.example.Netflix.Genre.Genre;
import com.example.Netflix.Resolutions.Resolution;
import com.example.Netflix.enums.ContentType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "contents")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Poster is required")
    private String poster;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Video link is required")
    private String videoLink;
    @NotNull
    private Double duration;

    @JsonDeserialize(using = StatusDeserializer.class)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ContentType type;

    private Integer season;
    private Integer episodeNumber;
    private Integer seriesId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToMany
    @JoinTable(
            name = "quality_ranges",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "resolution_id")
    )
    private List<Resolution> resolutions;

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

    public Content(String title,
                   String poster,
                   String description,
                   String videoLink,
                   Double duration,
                   ContentType type,
                   Integer season,
                   Integer episodeNumber,
                   Integer seriesId,
                   LocalDateTime updatedAt,
                   LocalDateTime createdAt,
                   List<Resolution> resolutions,
                   Set<Genre> genres) {
        this.title = title;
        this.poster = poster;
        this.description = description;
        this.videoLink = videoLink;
        this.duration = duration;
        this.type = type;
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.seriesId = seriesId;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.resolutions = resolutions;
        this.genres = genres;
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

    public List<Resolution> getResolutions() {
        return this.resolutions;
    }

    public void setResolutions(List<Resolution> resolutions) {
        this.resolutions = resolutions;
    }
}

