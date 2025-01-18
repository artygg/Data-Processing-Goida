package com.example.Netflix.Content;

import com.example.Netflix.Deserializer.StatusDeserializer;
import com.example.Netflix.Genre.Genre;
import com.example.Netflix.Resolutions.Resolution;
import com.example.Netflix.enums.ContentType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "contents")
public class Content
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;
    @NotBlank(message = "Title is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Title must contain only letters and numbers")
    private String title;
    @NotBlank(message = "Poster is required")
    private String poster;
    @NotBlank(message = "Description is required")
    @Pattern(regexp = "^[a-zA-Z0-9.!?\" ]+$", message = "Description format is invalid")
    private String description;
    @NotBlank(message = "Video link is required")
    private String videoLink;
    @NotNull(message = "Duration is required")
    @Pattern(regexp = "^[0-9.,]+$", message = "Duration format is invalid")
    private Double duration;

    @JsonDeserialize(using = StatusDeserializer.class)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Content type is required")
    private ContentType type;

    private Integer season;
    private Integer episodeNumber;
    private Integer seriesId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "quality_ranges",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "resolution_id")
    )
    private List<Resolution> resolutions;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "genre_contents",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres;

    public Content()
    {
    }

    public Content(String title, String poster, String description, String videoLink, Double duration, ContentType type, Integer season, Integer episodeNumber, Integer seriesId, LocalDateTime updatedAt, LocalDateTime createdAt, List<Resolution> resolutions, List<Genre> genres)
    {
        this.setTitle(title);
        this.setPoster(poster);
        this.setDescription(description);
        this.setVideoLink(videoLink);
        this.setDuration(duration);
        this.setType(type);
        this.setSeason(season);
        this.setEpisodeNumber(episodeNumber);
        this.setSeriesId(seriesId);
        this.setUpdatedAt(updatedAt);
        this.setCreatedAt(createdAt);
        this.setResolutions(resolutions);
        this.setGenres(genres);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new IllegalArgumentException("ID must be a positive number.");
        }
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        if (title == null || title.trim().isEmpty())
        {
            throw new IllegalArgumentException("Title is required and cannot be empty.");
        }
        this.title = title;
    }

    public String getPoster()
    {
        return poster;
    }

    public void setPoster(String poster)
    {
        if (poster == null || poster.trim().isEmpty())
        {
            throw new IllegalArgumentException("Poster is required and cannot be empty.");
        }
        this.poster = poster;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        if (description == null || description.trim().isEmpty())
        {
            throw new IllegalArgumentException("Description is required and cannot be empty.");
        }
        this.description = description;
    }

    public String getVideoLink()
    {
        return videoLink;
    }

    public void setVideoLink(String videoLink)
    {
        if (videoLink == null || videoLink.trim().isEmpty())
        {
            throw new IllegalArgumentException("Video link is required and cannot be empty.");
        }
        this.videoLink = videoLink;
    }

    public Double getDuration()
    {
        return duration;
    }

    public void setDuration(Double duration)
    {
        if (duration == null || duration <= 0)
        {
            throw new IllegalArgumentException("Duration must be a positive number.");
        }
        this.duration = duration;
    }

    public ContentType getType()
    {
        return type;
    }

    public void setType(ContentType type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("Content type is required.");
        }
        this.type = type;
    }

    public Integer getSeason()
    {
        return season;
    }

    public void setSeason(Integer season)
    {
        if (season != null && season < 1)
        {
            throw new IllegalArgumentException("Season number must be at least 1.");
        }
        this.season = season;
    }

    public Integer getEpisodeNumber()
    {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber)
    {
        if (episodeNumber != null && episodeNumber < 1)
        {
            throw new IllegalArgumentException("Episode number must be at least 1.");
        }
        this.episodeNumber = episodeNumber;
    }

    public Integer getSeriesId()
    {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId)
    {
        if (seriesId != null && seriesId < 1)
        {
            throw new IllegalArgumentException("Series ID must be a positive number.");
        }
        this.seriesId = seriesId;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
    {
        if (createdAt == null)
        {
            throw new IllegalArgumentException("Created timestamp cannot be null.");
        }
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt)
    {
        if (updatedAt == null)
        {
            throw new IllegalArgumentException("Updated timestamp cannot be null.");
        }
    }

    public List<Resolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}

