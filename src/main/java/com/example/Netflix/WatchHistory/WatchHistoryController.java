package com.example.Netflix.WatchHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchHistory")
public class WatchHistoryController {
    @Autowired
    private final WatchHistoryService watchHistoryService;

    public WatchHistoryController(WatchHistoryService watchHistoryService) {
        this.watchHistoryService = watchHistoryService;
    }

    @GetMapping("/profile/{profileId}")
    public List<WatchHistory> getWatchHistoryByProfileId(@PathVariable Long profileId) {
        return watchHistoryService.getWatchHistoryByProfileId(profileId);
    }

    @PostMapping
    public WatchHistory createWatchHistory(@RequestBody WatchHistory watchHistory) {
        return watchHistoryService.createWatchHistory(watchHistory);
    }
}
