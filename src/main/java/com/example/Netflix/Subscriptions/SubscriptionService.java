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
        Optional<Profile> profileOpt = profileRepository.findById(profileId);
        if (profileOpt.isEmpty()) {
            throw new IllegalArgumentException("Profile not found");
        }

        Profile profile = profileOpt.get();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);

        Subscription trialSubscription = new Subscription(profile, null, startDate, endDate);
        return subscriptionRepository.save(trialSubscription);
    }

    public Subscription createSubscription(UUID profileId, Integer priceId, LocalDate startDate, LocalDate endDate) {
        Optional<Profile> profileOpt = profileRepository.findById(profileId);
        if (profileOpt.isEmpty()) {
            throw new IllegalArgumentException("Profile not found");
        }

        Profile profile = profileOpt.get();
        double subscriptionCost = getSubscriptionCost(priceId);

        Subscription subscription = new Subscription(profile, (int) subscriptionCost, startDate, endDate);
        return subscriptionRepository.save(subscription);
    }

    public Optional<Subscription> getSubscriptionById(UUID id) {
        return subscriptionRepository.findById(id);
    }

    public void applyDiscountForInvitation(UUID inviterProfileId, UUID inviteeProfileId) {
        Profile inviter = profileRepository.findById(inviterProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Inviter profile not found"));
        Profile invitee = profileRepository.findById(inviteeProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Invitee profile not found"));

        Optional<Subscription> inviterSubscriptionOpt = subscriptionRepository.findLatestSubscriptionByProfileId(inviter.getId());
        Optional<Subscription> inviteeSubscriptionOpt = subscriptionRepository.findLatestSubscriptionByProfileId(invitee.getId());

        if (inviterSubscriptionOpt.isEmpty() || inviteeSubscriptionOpt.isEmpty()) {
            throw new IllegalArgumentException("One or both profiles do not have active subscriptions.");
        }

        Subscription inviterSubscription = inviterSubscriptionOpt.get();
        Subscription inviteeSubscription = inviteeSubscriptionOpt.get();

        if (isEligibleForDiscount(inviterSubscription) && isEligibleForDiscount(inviteeSubscription)) {
            inviterSubscription.setPriceId(inviterSubscription.getPriceId() - 2);
            inviteeSubscription.setPriceId(inviteeSubscription.getPriceId() - 2);
            subscriptionRepository.save(inviterSubscription);
            subscriptionRepository.save(inviteeSubscription);
        }
    }

    private double getSubscriptionCost(Integer priceId) {
        return switch (priceId) {
            case 1 -> 7.99;
            case 2 -> 10.99;
            case 3 -> 13.99;
            default -> throw new IllegalArgumentException("Invalid price ID");
        };
    }

    private boolean isEligibleForDiscount(Subscription subscription) {
        return subscription != null && subscription.getEndDate().isAfter(LocalDate.now());
    }
}
