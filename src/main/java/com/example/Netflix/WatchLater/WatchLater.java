package com.example.Netflix.WatchLater;

import com.example.Netflix.Content.Content;
import jakarta.persistence.*;

@Entity
@IdClass(WatchLaterId.class)
@Table(name = "watch_later")
public class WatchLater
{
    @Id
    private Long profileId;

    @Id
    private Long contentId;

    @ManyToOne
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private Content content;

    public WatchLater()
    {
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

    public Content getContent()
    {
        return content;
    }

    public void setContent(Content content)
    {
        this.content = content;
    }
}
