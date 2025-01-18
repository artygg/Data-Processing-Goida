package com.example.Netflix.Subscriptions;

import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.Subscriptions.RequestBody.UsersIdBody;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
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
            return ResponseEntity.badRequest().body(new ResponseMessage("Bad request"));
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createSubscription(@RequestBody @Valid SubscriptionDTO subscriptionBody) {
        Date start = null;
        Date end = null;

        if (subscriptionBody.getStartDate() != null) {
            start = subscriptionBody.getStartDate();
        }

        if (subscriptionBody.getEndDate() != null) {
            end = subscriptionBody.getEndDate();
        }
        try {
            Subscription subscription = subscriptionService.createSubscription(subscriptionBody.getProfile(), subscriptionBody.getPriceId(), start, end);
            return ResponseEntity.ok(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Bad request"));
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
            return ResponseEntity.badRequest().body(new ResponseMessage("Bad request"));
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