package com.example.Netflix.WatchHistory;

import com.example.Netflix.Generalization.BaseController;
import com.example.Netflix.Generalization.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/watchHistory")
public class WatchHistoryController extends BaseController<WatchHistory, Long> {

    @Autowired
    private final WatchHistoryService watchHistoryService;

    public WatchHistoryController(WatchHistoryService watchHistoryService) {
        this.watchHistoryService = watchHistoryService;
    }

    @Override
    protected BaseService<WatchHistory, Long> getService() {
        return watchHistoryService;
    }

    @GetMapping(value = "/profile/{profileId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<WatchHistory> getWatchHistoryByProfileId(@PathVariable UUID profileId) {
        return watchHistoryService.getWatchHistoryByProfileId(profileId);
    }
}