package com.example.Netflix.WatchLater;

import java.io.Serializable;
import java.util.Objects;

public class WatchLaterId implements Serializable
{
    private Long profileId;
    private Long contentId;

    public WatchLaterId()
    {
    }

    public WatchLaterId(Long profileId, Long contentId)
    {
        this.profileId = profileId;
        this.contentId = contentId;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        WatchLaterId that = (WatchLaterId) o;
        return Objects.equals(profileId, that.profileId) && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(profileId, contentId);
    }
}