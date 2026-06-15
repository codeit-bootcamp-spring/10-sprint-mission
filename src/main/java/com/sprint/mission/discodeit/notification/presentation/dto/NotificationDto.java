package com.sprint.mission.discodeit.notification.presentation.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationDto(UUID id,
                              Instant createdAt,
                              UUID receivedId,
                              String title,
                              String content) {

}
