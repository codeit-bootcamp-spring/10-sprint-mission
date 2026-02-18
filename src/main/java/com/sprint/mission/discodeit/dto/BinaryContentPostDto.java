package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentPostDto(
	UUID userId,
	UUID messageId,
	String fileName,
	byte[] data
) {
}
