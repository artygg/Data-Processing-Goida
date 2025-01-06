package com.example.Netflix.WatchLater;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchLaterRepository extends JpaRepository<WatchLater, WatchLaterId>
{}