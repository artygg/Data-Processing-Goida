package com.example.Netflix.Referals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReferralService {
    @Autowired
    private ReferralRepository referralRepository;

    public void saveReferral(Referral referral) {
        referralRepository.save(referral);
    }

    public Optional<Referral> getReferralByInvitedId(UUID id) {
        return  referralRepository.findReferralByInvitedId(id);
    }
}
