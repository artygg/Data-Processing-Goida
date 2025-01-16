package com.example.Netflix.Subscriptions;

import com.example.Netflix.Profiles.Profile;
import com.example.Netflix.Profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public Subscription startTrial(UUID profileId) {
        subscriptionRepository.createTrialSubscription(profileId);

        return subscriptionRepository.findLatestSubscriptionByProfileId(profileId).get();
    }

    public Subscription createSubscription2(UUID profileId, Integer priceId, Date startDate, Date endDate) {
        Double subscriptionCost = 0.0;

        subscriptionRepository.createSubscription(profileId, priceId, startDate, endDate, subscriptionCost);

        return subscriptionRepository.findLatestSubscriptionByProfileId(profileId).get();
    }

    public Optional<Subscription> getSubscriptionById(UUID id) {
        return subscriptionRepository.findById(id);
    }

    public void applyDiscountForInvitation(UUID inviterProfileId, UUID inviteeProfileId) {
        subscriptionRepository.applyDiscountForInvitation(inviterProfileId, inviteeProfileId);
    }
}
