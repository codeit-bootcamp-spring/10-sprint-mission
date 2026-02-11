package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@ToString
public class ReadStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private final UUID channelId;
    private final long createdAt = Instant.now().getEpochSecond();
    private long updatedAt = createdAt;
    private ReadType type = ReadType.UNREAD;

    public void update(ReadType type) {
        this.type = type;
        updatedAt = Instant.now().getEpochSecond();
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
                .type(type)
                .build();
    }
}
