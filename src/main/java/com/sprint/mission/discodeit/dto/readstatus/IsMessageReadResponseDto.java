package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record IsMessageReadResponseDto(
        boolean isRead,
        UUID userId,
        UUID messageId
) {
}
