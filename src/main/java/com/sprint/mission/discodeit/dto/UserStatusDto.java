package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {

  public record Create(
      UUID userId
  ) {

  }

  public record Response(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID userId,
      Instant lastActiveAt
  ) {

    public static Response of(UserStatus status) {
      return new Response(
          status.getId(),
          status.getCreatedAt(),
          status.getUpdatedAt(),
          status.getUserId(),
          status.getLastActiveAt()
      );
    }
  }

  public record Update(
      Instant newLastActiveAt
  ) {

  }
}
