package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentResponseDto(
	UUID userId,
	UUID messageId,
	byte[] data
) {
}
