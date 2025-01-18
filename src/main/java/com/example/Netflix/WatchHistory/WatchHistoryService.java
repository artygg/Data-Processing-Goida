package com.example.Netflix.WatchHistory;

import com.example.Netflix.Generalization.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WatchHistoryService extends BaseService<WatchHistory, Long> {
    private final WatchHistoryRepository watchHistoryRepository;

    public WatchHistoryService(WatchHistoryRepository watchHistoryRepository) {
        this.watchHistoryRepository = watchHistoryRepository;
    }

    @Override
    protected JpaRepository<WatchHistory, Long> getRepository() {
        return watchHistoryRepository;
    }

    public List<WatchHistory> getWatchHistoryByProfileId(UUID profileId) {
        return watchHistoryRepository.findAllByProfileId(profileId);
    }
}
