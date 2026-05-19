package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Role;

public record UserDto(
	UUID id,
	String username,
	String email,
	BinaryContentDto profile,
	Boolean online,
	Role role
) {

	public UserDto(UUID id, String username, String email, BinaryContentDto profile, Boolean online) {
		this(id, username, email, profile, online, Role.USER);
	}
}
