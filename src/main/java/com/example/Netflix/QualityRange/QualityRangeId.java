package com.example.Netflix.QualityRange;

import java.io.Serializable;
import java.util.Objects;

public class QualityRangeId implements Serializable
{
    private Long contentId;
    private Long resolutionId;

    public QualityRangeId()
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

    public Long getResolutionId()
    {
        return resolutionId;
    }

    public void setResolutionId(Long resolutionId)
    {
        this.resolutionId = resolutionId;
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
        QualityRangeId that = (QualityRangeId) o;
        return Objects.equals(contentId, that.contentId) && Objects.equals(resolutionId, that.resolutionId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(contentId, resolutionId);
    }
}