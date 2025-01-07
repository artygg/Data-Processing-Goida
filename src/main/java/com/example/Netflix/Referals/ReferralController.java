package com.example.Netflix.Referals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/referrals")
public class ReferralController {
    @Autowired
    private ReferralService referralService;

    @PostMapping
    public ResponseEntity<?> saveReferralProcedure(@RequestBody Referral referralBody) {
        Referral referral = new Referral();
        referral.setHostId(referralBody.getHostId());
        referral.setInvitedId(referralBody.getInvitedId());

        try {
            referralService.saveReferral(referral);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        return ResponseEntity.ok("Referral procedure was saved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReferralByInvitedId(@PathVariable Long id) {
        try {
            Optional<Referral> optionalReferral = referralService.getReferralByInvitedId(id);

            if (optionalReferral.isPresent()) {
                 Referral referral = optionalReferral.get();

                return ResponseEntity.ok(referral);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Referral was not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
