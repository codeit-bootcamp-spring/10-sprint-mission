package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusResponseDto {
    private final UUID id;
    private final UUID userID;
    private final UUID channelId;
    private final Instant lastReadAt;
}
