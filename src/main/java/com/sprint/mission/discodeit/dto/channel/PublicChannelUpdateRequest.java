package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "채널 수정 요청")
public record PublicChannelUpdateRequest(
    @Size(min = 1)
    @Schema(description = "새로운 채널 이름", example = "공부 채널")
    String newName,
    @Size(min = 1)
    @Schema(description = "새로운 채널 설명", example = "공부 채널입니다.")
    String newDescription
) {

}
