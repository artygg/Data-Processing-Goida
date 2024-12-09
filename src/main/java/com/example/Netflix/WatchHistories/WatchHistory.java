package com.example.Netflix.WatchHistories;

import com.example.Netflix.Content.Content;
import jakarta.persistence.*;

@Entity
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToOne
    private Content content;
    private String stoppedAt;
    private int progress;
    private int watchingTimes;

    public WatchHistory() {

    }

    public WatchHistory(Content content,
                        String stoppedAt,
                        int progress,
                        int watchingTimes) {
        this.content = content;
        this.stoppedAt = stoppedAt;
        this.progress = progress;
        this.watchingTimes = watchingTimes;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Content getContent() {
        return this.content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getStoppedAt() {
        return this.stoppedAt;
    }

    public void setStoppedAt(String stoppedAt) {
        this.stoppedAt = stoppedAt;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getWatchingTimes() {
        return this.watchingTimes;
    }

    public void setWatchingTimes(int watchingTimes) {
        this.watchingTimes = watchingTimes;
    }
}
