package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadStatusDto {

    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
}
