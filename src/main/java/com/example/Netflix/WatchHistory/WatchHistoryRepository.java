package com.example.Netflix.WatchHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long>
{
    List<WatchHistory> findAllByProfileId(UUID profileId);
}