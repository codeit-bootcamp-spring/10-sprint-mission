package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "수신 정보 수정 요청")
public record ReadStatusUpdateRequest(
    @Schema(description = "새로운 수신 시각", example = "2026-02-23T01:30:54Z")
    Instant newLastReadAt,
    @Schema(description = "새로운 채널 알림 여부", example = "true")
    boolean newNotificationEnabled
) {

}
