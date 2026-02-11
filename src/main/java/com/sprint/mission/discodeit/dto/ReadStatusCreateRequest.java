package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ReadStatusCreateRequest {
    private UUID userId;
    private UUID channelId;

    public ReadStatusCreateRequest(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }
}
