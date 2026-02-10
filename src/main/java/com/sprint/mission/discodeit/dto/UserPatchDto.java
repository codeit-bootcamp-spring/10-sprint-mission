package com.sprint.mission.discodeit.dto;

public record UserPatchDto(
	String nickName,
	String email,
	String phoneNumber,
	String password,
	BinaryContentDto binaryContentDTO
) {
}
