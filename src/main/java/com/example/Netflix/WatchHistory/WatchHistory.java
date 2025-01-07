package com.example.Netflix.WatchHistory;

import com.example.Netflix.Content.Content;
import jakarta.persistence.*;

@Entity
@Table(name = "watch_histories")
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id") // Explicit mapping
    private Long profileId;

    @Column(name = "content_id") // Explicit mapping
    private Long contentId;

    @Column(name = "stopped_at") // Explicit mapping
    private Double stoppedAt;

    @Column(name = "progress") // Explicit mapping
    private Double progress;

    @Column(name = "watching_times") // Explicit mapping
    private Integer watchingTimes;

    @ManyToOne
    @JoinColumn(name = "content_id", insertable = false, updatable = false) // Avoid duplicate mapping
    private Content content;

    public WatchHistory()
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

    public Long getProfileId()
    {
        return profileId;
    }

    public void setProfileId(Long profileId)
    {
        this.profileId = profileId;
    }

    public Long getContentId()
    {
        return contentId;
    }

    public void setContentId(Long contentId)
    {
        this.contentId = contentId;
    }

    public Double getStoppedAt()
    {
        return stoppedAt;
    }

    public void setStoppedAt(Double stoppedAt)
    {
        this.stoppedAt = stoppedAt;
    }

    public Double getProgress()
    {
        return progress;
    }

    public void setProgress(Double progress)
    {
        this.progress = progress;
    }

    public Integer getWatchingTimes()
    {
        return watchingTimes;
    }

    public void setWatchingTimes(Integer watchingTimes)
    {
        this.watchingTimes = watchingTimes;
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