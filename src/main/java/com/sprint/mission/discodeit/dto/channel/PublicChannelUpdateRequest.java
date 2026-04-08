package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(
        @Schema(description = "수정할 Channel 이름", example = "공지-수정")
        @Size(min = 1, max = 100, message = "newName은 1자 이상 100자 이하여야 합니다.")
        String newName,
        @Schema(description = "수정할 Channel 설명", example = "설명-수정")
        @Size(max = 500, message = "newDescription은 500자 이하여야 합니다.")
        String newDescription
) {}
