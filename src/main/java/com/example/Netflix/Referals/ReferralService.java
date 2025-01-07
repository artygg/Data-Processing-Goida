package com.example.Netflix.Referals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReferralService {
    @Autowired
    private ReferralRepository referralRepository;

    public void saveReferral(Referral referral) {
        referralRepository.save(referral);
    }

    public Optional<Referral> getReferralByInvitedId(Long id) {
        return  referralRepository.findReferralByInvitedId(id);
    }
}
