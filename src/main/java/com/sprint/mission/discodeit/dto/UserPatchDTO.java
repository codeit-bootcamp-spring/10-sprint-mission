package com.sprint.mission.discodeit.dto;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public record UserPatchDTO(
	UUID id,
	User updateData,
	BinaryContentDTO binaryContentDTO // 프로필 사진을 변경하는 경우
) {
}
