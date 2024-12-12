package com.example.Netflix.Subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @Query("SELECT s FROM Subscription s WHERE s.profile.id = :profileId AND s.endDate >= CURRENT_DATE ORDER BY s.startDate DESC")
    Optional<Subscription> findLatestSubscriptionByProfileId(UUID profileId);
}

