package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record MessageResponseDto(
	UUID id,
	String text,
	UUID authorId,
	UUID channelId
) {
}
