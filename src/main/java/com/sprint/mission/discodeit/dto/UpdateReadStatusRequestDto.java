package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ReadStatusUpdateRequest", description = "수정할 읽음 상태 정보")
public record UpdateReadStatusRequestDto(
        Instant newLastReadAt,
        Boolean newNotificationEnabled
) {
}
