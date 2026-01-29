package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.UUID;

public record ReadStatusDTO(
        UUID userId,
        UUID channelId
) {
    public static ReadStatusDTO of(ReadStatus status) {
        return new ReadStatusDTO(
                status.getUserId(),
                status.getChannelId()
        );
    }
}

