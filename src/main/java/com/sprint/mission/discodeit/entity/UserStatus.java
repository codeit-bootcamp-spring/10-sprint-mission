package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.util.TimeConverter;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusServiceDTO.UserStatusResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int ACTIVE_THRESHOLD = 300;
    @Getter
    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private final Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Instant lastActiveAt = Instant.now();

    public boolean matchUserId(UUID userId) {
        return this.userId.equals(userId);
    }

    public void update(LocalDateTime datetime) {
        lastActiveAt = TimeConverter.toInstant(datetime);
        updatedAt = Instant.now();
    }

    public boolean isActive() {
        return Duration.between(lastActiveAt, Instant.now()).getSeconds() < ACTIVE_THRESHOLD;
    }

    public UserStatusResponse toResponse() {
        return UserStatusResponse.builder()
                .id(id)
                .userId(userId)
                .createdAt(TimeConverter.toDateTime(createdAt))
                .updatedAt(TimeConverter.toDateTime(updatedAt))
                .lastActiveAt(TimeConverter.toDateTime(lastActiveAt))
                .online(isActive())
                .build();
    }
}
