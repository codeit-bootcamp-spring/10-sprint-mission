package com.sprint.mission.discodeit.dto.readstatusdto;

import java.util.UUID;

public record ReadStatusCreateRequestDTO(
        UUID channelId,
        UUID userId
) {
}
