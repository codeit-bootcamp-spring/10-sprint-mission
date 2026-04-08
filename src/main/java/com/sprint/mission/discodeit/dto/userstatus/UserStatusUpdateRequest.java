package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Schema(description = "변경할 User 온라인 상태 정보")
public record UserStatusUpdateRequest(
        @Schema(description = "변경할 마지막 활동 시각", example = "2026-02-20T11:25:00Z")
        @NotNull(message = "newLastActiveAt은 필수입니다.")
        Instant newLastActiveAt
) {}
