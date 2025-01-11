package com.example.Netflix.WatchLater;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchLater")
public class WatchLaterController {
    private final WatchLaterService watchLaterService;

    public WatchLaterController(WatchLaterService watchLaterService) {
        this.watchLaterService = watchLaterService;
    }

    @GetMapping(value = "/profile/{profileId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<WatchLater> getWatchLaterByProfileId(@PathVariable Long profileId) {
        return watchLaterService.getWatchLaterByProfileId(profileId);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public WatchLater addWatchLater(@RequestBody WatchLater watchLater) {
        return watchLaterService.addWatchLater(watchLater);
    }


    @DeleteMapping("/profile/{profileId}/content/{contentId}")
    public ResponseEntity<Void> deleteWatchLater(@PathVariable Long profileId, @PathVariable Long contentId) {
        watchLaterService.deleteWatchLater(profileId, contentId);
        return ResponseEntity.noContent().build();
    }
}