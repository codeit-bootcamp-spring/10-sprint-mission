package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
	UUID id,
	Instant createdAt,
	Instant updatedAt,
	String username,
	String email,
	UUID profileId,
	Boolean online
) {
}
