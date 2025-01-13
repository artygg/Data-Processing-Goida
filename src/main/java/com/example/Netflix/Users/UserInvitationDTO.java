package com.example.Netflix.Users;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UserInvitationDTO {
    @NotNull(message = "Id is required")
    private UUID hostID;

    public UserInvitationDTO(UUID hostID) {
        this.hostID = hostID;
    }

    public UUID getHostID() {
        return this.hostID;
    }

    public void setHostID(UUID hostID) {
        this.hostID = hostID;
    }
}
