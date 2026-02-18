package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ReadStatusPatchDto(
	UUID readStatusId,
	UUID userId,
	UUID channelId
) {
}
