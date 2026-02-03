package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@ToString
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Getter
    private final UUID id = UUID.randomUUID();
    @Getter
    private final UUID userId;
    private final UUID channelId;
    private final long createdAt = Instant.now().getEpochSecond();
    private long updatedAt = createdAt;
    private ReadType type = ReadType.UNREAD;

    public void setRead() {
        type = ReadType.READ;
        updatedAt = Instant.now().getEpochSecond();
    }

    public void setUnRead() {
        type = ReadType.UNREAD;
        updatedAt = Instant.now().getEpochSecond();
    }

    public boolean matchType(ReadType type) {
        return this.type == type;
    }

    public boolean matchChannelId(UUID channelId) {
        return this.channelId.equals(channelId);
    }
}
