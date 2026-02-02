package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReadStatusResponse {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private final Instant lastReadAt;

    public ReadStatusResponse(UUID id, UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
}
