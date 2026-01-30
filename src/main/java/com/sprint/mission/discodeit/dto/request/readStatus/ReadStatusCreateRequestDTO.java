package com.sprint.mission.discodeit.dto.request.readStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ReadStatusCreateRequestDTO {
    @NotNull
    private UUID userId;

    @NotNull
    private UUID channelId;

    public ReadStatusCreateRequestDTO(UUID memberId, UUID channelId) {
        this.userId = memberId;
        this.channelId = channelId;
    }
}
