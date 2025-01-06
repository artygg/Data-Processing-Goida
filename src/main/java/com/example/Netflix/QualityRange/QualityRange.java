package com.example.Netflix.QualityRange;

import com.example.Netflix.Content.Content;
import jakarta.persistence.*;

@Entity
@IdClass(QualityRangeId.class)
@Table(name = "quality_ranges")
public class QualityRange {
    @Id
    private Long contentId;

    @Id
    private Long resolutionId;

    private String resolutionName;

    @ManyToOne
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private Content content;

    public QualityRange()
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

    public String getResolutionName()
    {
        return resolutionName;
    }

    public void setResolutionName(String resolutionName)
    {
        this.resolutionName = resolutionName;
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