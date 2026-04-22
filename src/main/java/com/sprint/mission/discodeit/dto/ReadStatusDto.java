package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link ReadStatus}
 */
public record ReadStatusDto(UUID id, UUID userId, UUID channelId, Instant lastReadAt)
    implements Serializable {

}
