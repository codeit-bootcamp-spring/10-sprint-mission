package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatus extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }

    // 마지막으로 메시지 읽은 시간 저장
    public void updateLastReadAt(Instant lastReadAt){
        this.lastReadAt = lastReadAt;
        this.updated();
    }
}
