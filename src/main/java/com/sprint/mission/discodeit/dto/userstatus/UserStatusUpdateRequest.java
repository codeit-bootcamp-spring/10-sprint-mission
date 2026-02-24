package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "변경할 User 온라인 상태 정보")
public record UserStatusUpdateRequest(
    @Schema(description = "새로운 마지막 접속 시각", example = "2026-02-23T01:30:54Z")
    Instant newLastActiveAt
) {

}
