package com.example.Netflix.Referals;

import com.example.Netflix.JSON.ResponseMessage;
import com.example.Netflix.Users.User;
import com.example.Netflix.Users.UserRepository;
import com.example.Netflix.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReferralService {
    @Autowired
    private ReferralRepository referralRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    public ResponseEntity<?> saveReferral(UUID hostId, UUID invitedId) {
        try {
            referralRepository.saveReferral(hostId, invitedId);

            return ResponseEntity.ok("Referral created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public Referral getReferralByInvitedId(UUID invitedId) {
        return referralRepository.getReferralByInvitedId(invitedId);
    }

    public ResponseEntity<?> createReferral(UUID hostUserId, UUID invitedUserId) {
        try {
            userRepository.createReferral(hostUserId, invitedUserId);

            User hostUser = userService.findUserByUserId(hostUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Host user not found"));
            hostUser.setHasUsedReferralLink(true);
            userService.updateUser(hostUser);

            return ResponseEntity.ok(new ResponseMessage("Referral was successfully saved"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("An error occurred: " + e.getMessage()));
        }
    }
}
