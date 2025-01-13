package com.example.Netflix.Subscriptions;

import com.example.Netflix.Subscriptions.RequestBody.UsersIdBody;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> startTrial(@PathVariable UUID id) {
        try {
            Subscription trial = subscriptionService.startTrial(id);
            return ResponseEntity.ok(trial);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createSubscription(@RequestBody @Valid Subscription subscriptionBody) {
        LocalDate start = LocalDate.parse(subscriptionBody.getStartDate().toString());
        LocalDate end = (subscriptionBody.getEndDate() != null) ? LocalDate.parse(subscriptionBody.getEndDate().toString()) : null;

        try {
            Subscription subscription = subscriptionService.createSubscription(subscriptionBody.getProfile().getId(), subscriptionBody.getPriceId(), start, end);
            return ResponseEntity.ok(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/invite",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> inviteUserForDiscount(@RequestBody @Valid UsersIdBody usersIdBody) {
        try {
            subscriptionService.applyDiscountForInvitation(usersIdBody.getInviterProfileId(), usersIdBody.getInviteeProfileId());
            return ResponseEntity.ok("Discount applied successfully if eligible.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getSubscription(@PathVariable UUID id) {
        Optional<Subscription> subscription = subscriptionService.getSubscriptionById(id);
        return subscription.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}