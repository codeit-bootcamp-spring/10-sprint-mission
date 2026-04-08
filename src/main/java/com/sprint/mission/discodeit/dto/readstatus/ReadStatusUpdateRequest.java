package com.sprint.mission.discodeit.dto.readstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
@Schema(description = "수정할 읽음 상태 정보")
public record ReadStatusUpdateRequest(
        @Schema(description = "변경할 마지막 읽음 시각", example = "2026-02-20T12:05:00Z")
        @NotNull(message = "newLastReadAt은 필수입니다.")
        Instant newLastReadAt
) {}
