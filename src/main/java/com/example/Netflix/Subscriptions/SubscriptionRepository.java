package com.example.Netflix.Subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @Query("SELECT s FROM Subscription s WHERE s.profile.id = :profileId AND s.endDate >= CURRENT_DATE ORDER BY s.startDate DESC")
    Optional<Subscription> findLatestSubscriptionByProfileId(UUID profileId);

    @Modifying
    @Query(value = "CALL create_trial_subscription(:profileId, :subscriptionId)", nativeQuery = true)
    void createTrialSubscription(@Param("profileId") UUID profileId, @Param("subscriptionId") UUID subscriptionId);

    @Modifying
    @Query(value = "CALL create_subscription(:profileId, :priceId, :startDate, :endDate, :subscriptionId, :subscriptionCost)", nativeQuery = true)
    void createSubscription(@Param("profileId") UUID profileId,
                            @Param("priceId") Integer priceId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate,
                            @Param("subscriptionId") UUID subscriptionId,
                            @Param("subscriptionCost") Double subscriptionCost);

    @Modifying
    @Query(value = "CALL apply_discount_for_invitation(:inviterProfileId, :inviteeProfileId)", nativeQuery = true)
    void applyDiscountForInvitation(@Param("inviterProfileId") UUID inviterProfileId,
                                    @Param("inviteeProfileId") UUID inviteeProfileId);
}

