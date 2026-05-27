package com.sprint.mission.discodeit.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Public Channel 생성 정보")
public record ChannelCreateRequestPublic(
        @NotBlank(message = "채널 이름은 필수입니다.")
        @Schema(description = "채널 이름", example = "general")
        String name,
        @Schema(description = "채널 설명", example = "일반 채널입니다")
        String description
) {
}
