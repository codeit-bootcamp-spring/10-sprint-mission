package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "PublicChannelUpdateRequest", description = "수정할 Channel 정보")
public record UpdateChannelRequestDto(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @NotBlank(message = "채널 이름은 필수입니다.")
        String newName,
        String newDescription
) {
}
