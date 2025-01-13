package com.example.Netflix.WatchHistory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WatchHistoryService {
    private final WatchHistoryRepository watchHistoryRepository;

    public WatchHistoryService(WatchHistoryRepository watchHistoryRepository) {
        this.watchHistoryRepository = watchHistoryRepository;
    }

    public List<WatchHistory> getWatchHistoryByProfileId(UUID profileId) {
        return watchHistoryRepository.findAllByProfileId(profileId);
    }

    public WatchHistory createWatchHistory(WatchHistory watchHistory) {
        return watchHistoryRepository.save(watchHistory);
    }
}
