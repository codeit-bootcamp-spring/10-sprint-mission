package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(
        @Schema(description = "Public Channel 이름", example = "공지")
        @NotBlank(message = "name은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "name은 100자 이하여야 합니다.")
        String name,
        @Schema(description = "Public Channel 설명", example = "공지 채널")
        @Size(max = 500, message = "description은 500자 이하여야 합니다.")
        String description
) {
}
