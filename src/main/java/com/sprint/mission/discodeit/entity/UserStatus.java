package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserStatusServiceDTO.UserStatusResponse;
import lombok.Getter;
import lombok.NonNull;
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
    private final long ACTIVE_THRESHOLD = 300L;

    @Getter
    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private final long createdAt = Instant.now().getEpochSecond();
    private long updatedAt = createdAt;
    @Getter
    @NonNull
    private long lastActiveAt;

    public void update(long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        updatedAt = Instant.now().getEpochSecond();
    }

    public boolean isActive() {
        long now = Instant.now().getEpochSecond();
        return (now - lastActiveAt) < ACTIVE_THRESHOLD;
    }

    public UserStatusResponse toResponse() {
        return new UserStatusResponse(id, userId, lastActiveAt);
    }
}
