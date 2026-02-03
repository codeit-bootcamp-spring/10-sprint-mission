package com.sprint.mission.discodeit.dto.channeldto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDTO(
        UUID id,
        Instant latestMessageCreatedAt,
        Instant latestMessageUpdatedAt,
        List<UUID> userID
) {
}
