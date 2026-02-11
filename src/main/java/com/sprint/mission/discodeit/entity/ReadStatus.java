package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId; //누가
    private UUID channelId; //어떤 채널에서
    private Instant lastReadAt; //마지막으로 몇시에 읽었는지

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }

    //읽은 시간 업데이트
    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        this.updatedAt = Instant.now();
    }
}
