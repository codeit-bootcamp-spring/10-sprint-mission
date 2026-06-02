package com.sprint.mission.discodeit.dto.readstatusdto;

import java.time.Instant;

public record ReadStatusUpdateRequestDTO(
    Instant newLastReadAt,

    Boolean newNotificationEnabled
) {

}
