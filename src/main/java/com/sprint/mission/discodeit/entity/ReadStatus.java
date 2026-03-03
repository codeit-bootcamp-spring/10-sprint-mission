package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.util.TimeConverter;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ReadStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private final UUID channelId;
    private final Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    @NonNull
    private Instant lastReadAt;

    public void update(LocalDateTime datetime) {
        lastReadAt = TimeConverter.toInstant(datetime);
        updatedAt = Instant.now();
    }

    public boolean matchChannelId(UUID channelId) {
        return this.channelId.equals(channelId);
    }

    public boolean matchUserId(UUID userId) {
        return this.userId.equals(userId);
    }

    public ReadStatusResponse toResponse() {
        return ReadStatusResponse.builder()
                .id(id)
                .userId(userId)
                .channelId(channelId)
                .createdAt(TimeConverter.toDateTime(createdAt))
                .updatedAt(TimeConverter.toDateTime(updatedAt))
                .lastReadAt(TimeConverter.toDateTime(lastReadAt))
                .build();
    }
}
