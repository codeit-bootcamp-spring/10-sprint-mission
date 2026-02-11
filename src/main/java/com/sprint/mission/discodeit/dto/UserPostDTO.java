package com.sprint.mission.discodeit.dto;

public record UserPostDTO(
	String nickName,
	String userName,
	String email,
	String phoneNumber,
	String password,
	BinaryContentDTO profileImage
) {
}
