package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        Instant createdAt,
        /// 알림을 수신할 user의 id
        UUID receiverId,
        String title,
        String content
) {
}
