package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
    UUID id,
    UUID userId,
    Boolean isOnline,
    Instant lastActiveAt,
    Instant createdAt,
    Instant updatedAt
) {

  public static UserStatusResponse from(UserStatus userStatus) {
    return new UserStatusResponse(
        userStatus.getId(),
        userStatus.getUserId(),
        userStatus.isOnline(),
        userStatus.getLastActiveAt(),
        userStatus.getCreatedAt(),
        userStatus.getUpdatedAt()
    );
  }
}
