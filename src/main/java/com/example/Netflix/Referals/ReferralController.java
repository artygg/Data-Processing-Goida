package com.example.Netflix.Referals;

import com.example.Netflix.Generalization.BaseController;
import com.example.Netflix.Generalization.BaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/referrals")
public class ReferralController extends BaseController<Referral, Long> {
    @Autowired
    private ReferralService referralService;

    @Override
    protected BaseService<Referral, Long> getService() {
        return referralService;
    }

    @GetMapping(value = "/invited/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getReferralByInvitedId(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(referralService.getReferralByInvitedId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
