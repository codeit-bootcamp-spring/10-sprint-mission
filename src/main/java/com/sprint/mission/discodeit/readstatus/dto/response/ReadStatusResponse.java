package com.sprint.mission.discodeit.readstatus.dto.response;

import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link ReadStatus}
 */
public record ReadStatusResponse(UUID id, UUID userId, UUID channelId, Instant lastReadAt)
    implements Serializable {

}
