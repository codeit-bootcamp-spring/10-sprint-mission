package com.sprint.mission.discodeit.dto.readStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusResponseDto {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadTime;
}
