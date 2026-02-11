package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentResponseDTO(
	UUID userId,
	UUID messageId,
	byte[] data
) {
}
