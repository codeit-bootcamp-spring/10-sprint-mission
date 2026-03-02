package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(
        @Schema(description = "수정할 Channel 이름", example = "공지-수정")
        String newName,
        @Schema(description = "수정할 Channel 설명", example = "설명-수정")
        String newDescription
) {}
