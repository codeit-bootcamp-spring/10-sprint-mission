package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponseDTO(
	UUID userId,
	UUID channelId,
	Instant lastReadTime
) {
}
