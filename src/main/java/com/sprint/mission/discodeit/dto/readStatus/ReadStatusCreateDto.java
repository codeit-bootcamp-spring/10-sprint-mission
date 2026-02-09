package com.sprint.mission.discodeit.dto.readStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ReadStatusCreateDto {
    private UUID userId;
    private UUID channelId;
}
