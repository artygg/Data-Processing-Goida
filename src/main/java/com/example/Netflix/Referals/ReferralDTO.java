package com.example.Netflix.Referals;

import java.util.UUID;

public class ReferralDTO {
    private Long referralIdOut;
    private UUID hostIdOut;
    private UUID invitedIdOut;

    public Long getReferralIdOut()
    {
        return this.referralIdOut;
    }

    public void setReferralIdOut(Long referralIdOut)
    {
        this.referralIdOut = referralIdOut;
    }

    public UUID getHostIdOut()
    {
        return this.hostIdOut;
    }

    public void setHostIdOut(UUID hostIdOut)
    {
        this.hostIdOut = hostIdOut;
    }

    public UUID getInvitedIdOut()
    {
        return this.invitedIdOut;
    }

    public void setInvitedIdOut(UUID invitedIdOut)
    {
        this.invitedIdOut = invitedIdOut;
    }
}
