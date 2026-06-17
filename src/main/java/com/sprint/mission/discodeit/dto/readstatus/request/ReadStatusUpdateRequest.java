package com.sprint.mission.discodeit.dto.readstatus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "수정할 읽음 상태 정보")
public record ReadStatusUpdateRequest(
        Instant newLastReadAt,

        Boolean newNotificationEnabled
) {
}
