package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Public 채널 생성 요청")
public record PublicChannelCreateRequest(
    @NotBlank
    @Schema(description = "채널 이름", example = "게임 채널")
    String name,
    @NotBlank
    @Schema(description = "채널 설명", example = "게임 채널입니다.")
    String description
) {

}
