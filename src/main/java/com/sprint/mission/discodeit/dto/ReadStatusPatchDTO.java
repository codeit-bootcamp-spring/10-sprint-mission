package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ReadStatusPatchDTO(
	UUID readStatusId,
	UUID userId,
	UUID channelId
) {
}
