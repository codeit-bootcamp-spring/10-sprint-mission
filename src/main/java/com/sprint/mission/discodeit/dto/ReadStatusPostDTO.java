package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ReadStatusPostDTO(
	UUID userId,
	UUID channelId
) {
}
