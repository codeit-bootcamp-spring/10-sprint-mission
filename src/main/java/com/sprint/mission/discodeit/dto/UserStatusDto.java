package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link UserStatus}
 */
public record UserStatusDto(UUID id, UUID userId, LocalDateTime lastActiveAt)
    implements Serializable {

}
