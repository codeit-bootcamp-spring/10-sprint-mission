package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record UserResponseDto(
	UUID userId,
	UUID profileId,
	String nickName,
	String userName,
	String email,
	String phoneNumber,
	List<UUID> channelIds
) {
}
