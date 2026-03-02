package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(
        @Schema(description = "Public Channel 이름", example = "공지")
        String name,
        @Schema(description = "Public Channel 설명", example = "공지 채널")
        String description
) {
}
