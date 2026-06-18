package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    Instant createdAt,
    UUID receiverId,  // 알림을 수신할 userId
    String title,
    String content
) {

}
