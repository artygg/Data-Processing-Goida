package com.example.Netflix.Subscriptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/start-trial")
    public ResponseEntity<?> startTrial(@RequestParam UUID profileId) {
        try {
            Subscription trial = subscriptionService.startTrial(profileId);
            return ResponseEntity.ok(trial);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createSubscription(
            @RequestParam UUID profileId,
            @RequestParam Integer priceId,
            @RequestParam String startDate,
            @RequestParam(required = false) String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : null;

        try {
            Subscription subscription = subscriptionService.createSubscription(profileId, priceId, start, end);
            return ResponseEntity.ok(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteUserForDiscount(@RequestParam UUID inviterProfileId, @RequestParam UUID inviteeProfileId) {
        try {
            subscriptionService.applyDiscountForInvitation(inviterProfileId, inviteeProfileId);
            return ResponseEntity.ok("Discount applied successfully if eligible.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubscription(@PathVariable UUID id) {
        Optional<Subscription> subscription = subscriptionService.getSubscriptionById(id);
        return subscription.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
