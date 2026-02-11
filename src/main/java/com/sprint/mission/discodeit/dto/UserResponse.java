package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private boolean isOnline;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponse(UUID id, String name, String email, boolean isOnline, UUID profileId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isOnline = isOnline;
        this.profileId = profileId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
