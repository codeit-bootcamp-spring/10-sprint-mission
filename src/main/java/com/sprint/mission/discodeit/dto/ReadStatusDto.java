package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link ReadStatus}
 */
public record ReadStatusDto(UUID id, UUID userId, UUID channelId, LocalDateTime lastReadAt)
    implements Serializable {

}
