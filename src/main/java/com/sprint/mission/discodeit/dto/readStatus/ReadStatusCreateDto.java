package com.sprint.mission.discodeit.dto.readStatus;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReadStatusCreateDto {
    private UUID userId;
    private UUID channelId;
}
