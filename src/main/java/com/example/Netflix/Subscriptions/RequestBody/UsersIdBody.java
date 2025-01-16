package com.example.Netflix.Subscriptions.RequestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UsersIdBody {
    @NotNull
    private UUID inviterProfileId;
    @NotNull
    private UUID inviteeProfileId;

    public UsersIdBody() {

    }

    public UsersIdBody(UUID inviterProfileId, UUID inviteeProfileId) {
        this.inviterProfileId = inviterProfileId;
        this.inviteeProfileId = inviteeProfileId;
    }

    public UUID getInviterProfileId() {
        return this.inviterProfileId;
    }

    public void setInviterProfileId(UUID inviterProfileId) {
        this.inviterProfileId = inviterProfileId;
    }

    public UUID getInviteeProfileId() {
        return this.inviteeProfileId;
    }

    public void setInviteeProfileId(UUID inviteeProfileId) {
        this.inviteeProfileId = inviteeProfileId;
    }
}
