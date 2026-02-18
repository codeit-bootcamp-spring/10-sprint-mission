package com.sprint.mission.discodeit.dto;

public record UserPostDto(
	String nickName,
	String userName,
	String email,
	String phoneNumber,
	String password,
	BinaryContentDto profileImage
) {
}
