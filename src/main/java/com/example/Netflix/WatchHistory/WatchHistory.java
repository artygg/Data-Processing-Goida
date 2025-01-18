package com.example.Netflix.WatchHistory;

import com.example.Netflix.Content.Content;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "watch_histories")
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id")
    @NotNull(message = "Profile id required")
    private UUID profileId;

    @Column(name = "content_id")
    @NotNull(message = "Content is required")
    private Long contentId;

    @Column(name = "stopped_at")
    @NotNull(message = "Time stamp is required")
    private Double stoppedAt;

    @Column(name = "progress")
    @NotNull(message = "Progress required")
    private Double progress;

    @Column(name = "watching_times")
    @NotNull(message = "Watching times required")
    private Integer watchingTimes;

    @ManyToOne
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private Content content;

    public WatchHistory() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Double getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(Double stoppedAt) {
        this.stoppedAt = stoppedAt;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Integer getWatchingTimes() {
        return watchingTimes;
    }

    public void setWatchingTimes(Integer watchingTimes) {
        this.watchingTimes = watchingTimes;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}