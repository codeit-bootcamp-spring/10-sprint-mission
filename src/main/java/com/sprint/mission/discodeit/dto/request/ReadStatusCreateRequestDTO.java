package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReadStatusCreateRequestDTO {
    private UUID userId;
    private UUID channelId;

    public ReadStatusCreateRequestDTO(UUID memberId, UUID id) {
        this.userId = id;
        this.channelId = id;
    }
}
