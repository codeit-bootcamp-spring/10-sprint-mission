package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ReadStatusResponse {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatusResponse(UUID id, UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
}
