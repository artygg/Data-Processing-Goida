package com.example.Netflix.Referals;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long hostId;
    private Long invitedId;

    public Referral() {

    }

    public Referral(
            Long hostId,
            Long invitedId
    ) {
        this.hostId = hostId;
        this.invitedId = invitedId;
    }

    public Long getInvitedId() {
        return this.invitedId;
    }

    public void setInvitedId(Long invitedId) {
        this.invitedId = invitedId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHostId() {
        return this.hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }
}
