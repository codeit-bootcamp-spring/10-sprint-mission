package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
	@NotNull(message = "새로운 마지막 활동 시각을 확인할 수 없습니다.")
	Instant newLastActiveAt
) {

}
