package com.example.Netflix.Subscriptions;

import com.example.Netflix.Profiles.Profile;
import com.example.Netflix.Profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public Subscription startTrial(UUID profileId) {
        subscriptionRepository.createTrialSubscription(profileId, null);

        return subscriptionRepository.findLatestSubscriptionByProfileId(profileId).get();
    }

    public Subscription createSubscription(UUID profileId, Integer priceId, LocalDate startDate, LocalDate endDate) {
        UUID subscriptionId = null;
        Double subscriptionCost = 0.0;

        subscriptionRepository.createSubscription(profileId, priceId, startDate, endDate, subscriptionId, subscriptionCost);

        return subscriptionRepository.findLatestSubscriptionByProfileId(profileId).get();
    }

    public Optional<Subscription> getSubscriptionById(UUID id) {
        return subscriptionRepository.findById(id);
    }

    public void applyDiscountForInvitation(UUID inviterProfileId, UUID inviteeProfileId) {
        subscriptionRepository.applyDiscountForInvitation(inviterProfileId, inviteeProfileId);
    }

    private boolean isEligibleForDiscount(Subscription subscription) {
        return subscription != null && subscription.getEndDate().isAfter(LocalDate.now());
    }
}
