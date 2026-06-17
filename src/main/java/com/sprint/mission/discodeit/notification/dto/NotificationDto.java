package com.sprint.mission.discodeit.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    Instant createdAt,
    UUID receiverId,
    String title,
    String content
) {

}
