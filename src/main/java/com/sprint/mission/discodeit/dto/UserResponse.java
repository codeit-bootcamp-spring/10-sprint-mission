package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private UUID profileImageId; // null 가능
    private boolean isOnline;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponse(UUID id, String username, String email, UUID profileImageId,
                        boolean isOnline, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileImageId = profileImageId;
        this.isOnline = isOnline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

//    // Getters
//    public UUID getId() { return id; }
//    public String getUsername() { return username; }
//    public String getEmail() { return email; }
//    public UUID getProfileImageId() { return profileImageId; }
//    public boolean isOnline() { return isOnline; }
//    public Instant getCreatedAt() { return createdAt; }
//    public Instant getUpdatedAt() { return updatedAt; }
}
