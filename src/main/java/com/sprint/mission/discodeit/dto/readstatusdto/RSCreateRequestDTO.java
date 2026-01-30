package com.sprint.mission.discodeit.dto.readstatusdto;

import java.util.UUID;

public record RSCreateRequestDTO(
        UUID channelId,
        UUID userId
) {
}
