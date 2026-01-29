package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class ReadStatus {
    private final UUID id = UUID.randomUUID();
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
}
