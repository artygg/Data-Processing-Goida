package com.example.Netflix.WatchHistory;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/watchHistory")
public class WatchHistoryController {

    @Autowired
    private final WatchHistoryService watchHistoryService;

    public WatchHistoryController(WatchHistoryService watchHistoryService) {
        this.watchHistoryService = watchHistoryService;
    }

    @GetMapping(value = "/profile/{profileId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<WatchHistory> getWatchHistoryByProfileId(@PathVariable UUID profileId) {
        return watchHistoryService.getWatchHistoryByProfileId(profileId);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public WatchHistory createWatchHistory(@RequestBody @Valid WatchHistory watchHistory) {
        return watchHistoryService.createWatchHistory(watchHistory);
    }
}