package com.example.Netflix.WatchLater;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchLaterService {
    private final WatchLaterRepository watchLaterRepository;

    public WatchLaterService(WatchLaterRepository watchLaterRepository) {
        this.watchLaterRepository = watchLaterRepository;
    }

    public List<WatchLater> getWatchLaterByProfileId(Long profileId) {
        return watchLaterRepository.findAllByProfileId(profileId);
    }

    public WatchLater addWatchLater(WatchLater watchLater) {
        return watchLaterRepository.save(watchLater);
    }

    public void deleteWatchLater(Long profileId, Long contentId) {
        watchLaterRepository.deleteById(new WatchLaterId(profileId, contentId));
    }
}