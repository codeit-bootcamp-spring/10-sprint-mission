package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserPostDTO(
	UUID profileId,
	String nickName,
	String userName,
	String email,
	String phoneNumber,
	String password
) {
}
