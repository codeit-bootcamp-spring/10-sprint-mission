package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(
    UUID id,
    UUID userId,
    UUID channelId,
    Instant lastReadAt,
    Boolean notificationEnabled
) {

  public record ReadStatusCreateRequest(UUID userId, UUID channelId, Instant lastReadAt) {

  }

  public record ReadStatusUpdateRequest(Instant newLastReadAt, Boolean newNotificationEnabled) {

  }
}