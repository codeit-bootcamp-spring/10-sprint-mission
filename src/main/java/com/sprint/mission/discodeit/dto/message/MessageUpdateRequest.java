package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "수정할 Message 내용")
public record MessageUpdateRequest(
        @Schema(description = "수정할 Message 본문", example = "updated content")
        @NotBlank(message = "newContent는 비어 있을 수 없습니다.")
        String newContent
) {}
