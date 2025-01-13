package com.example.Netflix.Referals;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Entity
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Host id is required")
    private UUID hostId;
    @NotBlank(message = "Invited user id is required")
    private UUID invitedId;

    public Referral() {

    }

    public Referral(
            UUID hostId,
            UUID invitedId
    ) {
        this.hostId = hostId;
        this.invitedId = invitedId;
    }

    public UUID getInvitedId() {
        return this.invitedId;
    }

    public void setInvitedId(UUID invitedId) {
        this.invitedId = invitedId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getHostId() {
        return this.hostId;
    }

    public void setHostId(UUID hostId) {
        this.hostId = hostId;
    }
}
