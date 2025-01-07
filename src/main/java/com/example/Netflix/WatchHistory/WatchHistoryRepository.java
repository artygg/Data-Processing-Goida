package com.example.Netflix.WatchHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long>
{
    List<WatchHistory> findAllByProfileId(Long profileId);
}