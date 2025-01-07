package com.example.Netflix.WatchLater;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchLaterRepository extends JpaRepository<WatchLater, WatchLaterId>
{
    List<WatchLater> findAllByProfileId(Long profileId);
}