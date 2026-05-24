package com.sprint.mission.discodeit.userstatus.dto;

import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link UserStatus}
 */
public record UserStatusDto(UUID id, UUID userId, Instant lastActiveAt)
    implements Serializable {

}
