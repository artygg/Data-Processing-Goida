//package com.example.Netflix.Subtitle;
//
//import com.example.Netflix.Content.Content;
//import jakarta.persistence.*;
//
//@Entity
//@IdClass(SubtitleId.class)
//@Table(name = "subtitles")
//public class Subtitle {
//    @Id
//    private Long contentId;
//
//    @Id
//    private String language;
//
//    private String subtitleFilePath;
//
//    @ManyToOne
//    @JoinColumn(name = "content_id", insertable = false, updatable = false)
//    private Content content;
//
//    public Subtitle()
//    {
//    }
//
//    public Long getContentId()
//    {
//        return contentId;
//    }
//
//    public void setContentId(Long contentId)
//    {
//        this.contentId = contentId;
//    }
//
//    public String getLanguage()
//    {
//        return language;
//    }
//
//    public void setLanguage(String language)
//    {
//        this.language = language;
//    }
//
//    public String getSubtitleFilePath()
//    {
//        return subtitleFilePath;
//    }
//
//    public void setSubtitleFilePath(String subtitleFilePath)
//    {
//        this.subtitleFilePath = subtitleFilePath;
//    }
//
//    public Content getContent()
//    {
//        return content;
//    }
//
//    public void setContent(Content content)
//    {
//        this.content = content;
//    }
//}