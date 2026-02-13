package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@ToString
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @ToString.Exclude
    private final long ACTIVE_THRESHOLD = 300L;
    @Getter
    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private final long createdAt = Instant.now().getEpochSecond();
    private long updatedAt = createdAt;
    private long lastActiveAt = createdAt;

    public boolean matchUserId(UUID userId) {
        return this.userId.equals(userId);
    }

    public void update() {
        lastActiveAt = Instant.now().getEpochSecond();
        updatedAt = lastActiveAt;
    }

    public boolean isActive() {
        long now = Instant.now().getEpochSecond();
        return (now - lastActiveAt) < ACTIVE_THRESHOLD;
    }

    public UserStatusResponse toResponse() {
        return new UserStatusResponse(id, userId, isActive());
    }
}
