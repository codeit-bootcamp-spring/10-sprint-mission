package com.sprint.mission.discodeit.message.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
    Instant newLastReadAt,
    Boolean newNotificationEnabled
) {

}
