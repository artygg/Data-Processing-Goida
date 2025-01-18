package com.example.Netflix.Referals;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Host id is required")
    @Column(name = "host_id")
    private UUID hostId;

    @NotNull(message = "Invited user id is required")
    @Column(name = "invited_id")
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
